import cv2
import numpy as np
import subprocess
import os

class VideoCropper:
    def __init__(self, target_ratio=(9, 16), target_resolution=(720, 1280)):
        self.target_ratio = target_ratio
        self.target_resolution = target_resolution
        
    def generate_ffmpeg_command(self, video_path, output_path, is_dynamic=False):
        """Mantiene la lógica del Modo Seguro (Center Crop estático)."""
        if not output_path.lower().endswith('.mp4'):
            output_path = output_path.rsplit('.', 1)[0] + '.mp4'

        filtro_base = f"crop=ih*9/16:ih:(iw-ih*9/16)/2:0,scale={self.target_resolution[0]}:{self.target_resolution[1]}"
        cmd = f"ffmpeg -i \"{video_path}\" -vf \"{filtro_base}\" -c:v libx264 -c:a aac -y \"{output_path}\""
        return cmd

    def apply_dynamic_crop(self, video_path, output_path, trajectory, total_frames, fps):
        if not trajectory:
            raise ValueError("La trayectoria está vacía. No se puede ejecutar el tracking.")

        print("INFO: [Render] Calculando límites temporales (Heurística de Bordes y Área)...")
        
        # 1. Leemos el video primero para conocer sus límites físicos reales
        cap = cv2.VideoCapture(video_path)
        orig_w = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
        orig_h = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
        
        trajectory_matrix = np.zeros((total_frames, 4))
        for f_idx, box in trajectory.items():
            trajectory_matrix[f_idx] = box

        # --- NUEVO CÁLCULO DE INICIO (ÁREA + BORDES) ---
        widths = trajectory_matrix[:, 2] - trajectory_matrix[:, 0]
        heights = trajectory_matrix[:, 3] - trajectory_matrix[:, 1]
        areas = widths * heights
        
        max_area = np.max(areas)
        
        if max_area == 0:
            raise ValueError("El sujeto no tiene un área válida en ningún frame.")

        # Regla 1: Debe alcanzar al menos el 50% de su tamaño máximo (más estricto)
        threshold_area = max_area * 0.05 
        
        # Regla 2: El sujeto NO debe estar "cortado" por los bordes laterales del video
        # (Si x1 es menor a 10, o x2 está a menos de 10 pixeles del final, lo ignoramos)
        margin = 10
        not_at_edge = (trajectory_matrix[:, 0] > margin) & (trajectory_matrix[:, 2] < (orig_w - margin))
        
        # Aplicamos ambas reglas estrictas
        valid_mask = (areas >= threshold_area) & not_at_edge
        active_frames = np.where(valid_mask)[0]
        
        if len(active_frames) == 0:
            print("WARN: Regla de bordes muy estricta. Aplicando Fallback.")
            active_frames = np.where(areas >= (max_area * 0.30))[0]
            if len(active_frames) == 0:
                active_frames = np.where(areas > 0)[0]

        start_frame = int(active_frames[0])
        end_frame = int(active_frames[-1])
        start_time_sec = start_frame / fps

        print(f"INFO: [Render] Protagonista aislado. El video iniciará en el frame {start_frame} ({start_time_sec:.2f}s).")

        mask_detected = np.zeros(total_frames, dtype=bool)
        mask_detected[start_frame:end_frame + 1] = (areas[start_frame:end_frame + 1] > 0)

        crop_h = orig_h
        crop_w = int(crop_h * (self.target_ratio[0] / self.target_ratio[1]))
        
        temp_video = output_path.replace(".mp4", "_silent_temp.mp4")
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        out = cv2.VideoWriter(temp_video, fourcc, fps, self.target_resolution)
        
        # Interpolar y suavizar 
        centers_xy = np.zeros((total_frames, 2))
        centers_xy[:, 0] = (trajectory_matrix[:, 0] + trajectory_matrix[:, 2]) / 2
        
        idx = np.arange(total_frames)
        centers_xy[~mask_detected, 0] = np.interp(idx[~mask_detected], idx[mask_detected], centers_xy[mask_detected, 0])
            
        smoothed_cx = centers_xy[:, 0]

        window_size = min(30, len(smoothed_cx))
        if window_size > 1:
            weights = np.ones(window_size)/window_size
            smoothed_cx = np.convolve(smoothed_cx, weights, mode='same')
            smoothed_cx[:window_size//2] = centers_xy[:window_size//2, 0]
            smoothed_cx[-window_size//2:] = centers_xy[-window_size//2:, 0]

        print("INFO: [Render] Renderizando video con precisión estricta de frame...")
        
        cap.set(cv2.CAP_PROP_POS_FRAMES, 0)
        current_frame = 0
        
        while cap.isOpened() and current_frame <= end_frame:
            ret, frame = cap.read()
            if not ret: break
            
            if current_frame < start_frame:
                current_frame += 1
                continue
            
            cx = int(smoothed_cx[current_frame])
            start_x = cx - (crop_w // 2)
            
            if start_x < 0:
                start_x = 0
            elif start_x > (orig_w - crop_w):
                start_x = orig_w - crop_w
                
            start_y = int((orig_h - crop_h) / 2)
            
            cropped_frame = frame[start_y:start_y+crop_h, start_x:start_x+crop_w]
            resized_frame = cv2.resize(cropped_frame, self.target_resolution)
            
            out.write(resized_frame)
            current_frame += 1
            
        cap.release()
        out.release()
        
        print("INFO: [FFmpeg] Sincronizando audio con el nuevo inicio temporal...")
        cmd = f"ffmpeg -i \"{temp_video}\" -ss {start_time_sec:.3f} -i \"{video_path}\" -c:v libx264 -c:a aac -map 0:v:0 -map 1:a:0? -shortest -y \"{output_path}\""
        proceso = subprocess.run(cmd, shell=True, capture_output=True, text=True)
        
        if os.path.exists(temp_video):
            os.remove(temp_video)
            
        if proceso.returncode != 0:
            raise RuntimeError(f"Fallo en FFmpeg durante la sincronización: {proceso.stderr}")
            
        return cmd