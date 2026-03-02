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
        cmd = f"ffmpeg -i {video_path} -vf \"{filtro_base}\" -c:v libx264 -c:a aac -y {output_path}"
        return cmd

    def apply_dynamic_crop(self, video_path, output_path, trajectory, total_frames, fps):
        """
        Aplica suavizado, recorta solo la línea de tiempo donde el sujeto está presente,
        y sincroniza el audio aplicando un offset en FFmpeg.
        """
        if not trajectory:
            raise ValueError("La trayectoria está vacía. No se puede ejecutar el tracking.")

        print("INFO: [Render] Calculando límites temporales del sujeto...")
        
        # 1. Definir el inicio y el fin de la presencia de la persona
        start_frame = min(trajectory.keys())
        end_frame = max(trajectory.keys())
        start_time_sec = start_frame / fps

        print(f"INFO: [Render] Sujeto activo desde frame {start_frame} hasta {end_frame} (Inicio en seg: {start_time_sec:.2f}).")

        cap = cv2.VideoCapture(video_path)
        orig_w = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
        orig_h = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
        
        crop_h = orig_h
        crop_w = int(crop_h * (self.target_ratio[0] / self.target_ratio[1]))
        
        temp_video = output_path.replace(".mp4", "_silent_temp.mp4")
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        out = cv2.VideoWriter(temp_video, fourcc, fps, self.target_resolution)
        
        # 2. Interpolar y suavizar trayectoria (Cálculo Vectorial)
        trajectory_matrix = np.zeros((total_frames, 4))
        for f_idx, box in trajectory.items():
            trajectory_matrix[f_idx] = box

        centers_xy = np.zeros((total_frames, 2))
        centers_xy[:, 0] = (trajectory_matrix[:, 0] + trajectory_matrix[:, 2]) / 2
        
        mask_detected = (trajectory_matrix[:, 0] > 0)
        idx = np.arange(total_frames)
        
        if mask_detected.any():
            centers_xy[~mask_detected, 0] = np.interp(idx[~mask_detected], idx[mask_detected], centers_xy[mask_detected, 0])
        else:
            centers_xy[:, 0] = orig_w // 2
            
        smoothed_cx = centers_xy[:, 0]

        window_size = min(30, len(smoothed_cx))
        if window_size > 1:
            weights = np.ones(window_size)/window_size
            smoothed_cx = np.convolve(smoothed_cx, weights, mode='same')
            smoothed_cx[:window_size//2] = centers_xy[:window_size//2, 0]
            smoothed_cx[-window_size//2:] = centers_xy[-window_size//2:, 0]

        # 3. Renderizado Optimizado (Saltando el tiempo muerto)
        print("INFO: [Render] Renderizando video recortado temporalmente...")
        
        # Le decimos a OpenCV que salte directamente al frame donde la persona aparece
        cap.set(cv2.CAP_PROP_POS_FRAMES, start_frame)
        frame_idx = start_frame
        
        # El renderizado termina exactamente cuando la persona sale de escena
        while cap.isOpened() and frame_idx <= end_frame:
            ret, frame = cap.read()
            if not ret: break
            
            cx = int(smoothed_cx[frame_idx])
            start_x = cx - (crop_w // 2)
            
            if start_x < 0:
                start_x = 0
            elif start_x > (orig_w - crop_w):
                start_x = orig_w - crop_w
                
            start_y = int((orig_h - crop_h) / 2)
            
            cropped_frame = frame[start_y:start_y+crop_h, start_x:start_x+crop_w]
            resized_frame = cv2.resize(cropped_frame, self.target_resolution)
            
            out.write(resized_frame)
            frame_idx += 1
            
        cap.release()
        out.release()
        
        # 4. Sincronización de Audio Híbrida
        print("INFO: [FFmpeg] Sincronizando audio con el nuevo inicio temporal...")
        # El parámetro '-ss' en la SEGUNDA entrada (-i video_path) recorta el audio original
        # exactamente para que encaje con el video procesado.
        cmd = f"ffmpeg -i {temp_video} -ss {start_time_sec:.3f} -i {video_path} -c:v libx264 -c:a aac -map 0:v:0 -map 1:a:0 -shortest -y {output_path}"
        proceso = subprocess.run(cmd, shell=True, capture_output=True, text=True)
        
        if os.path.exists(temp_video):
            os.remove(temp_video)
            
        if proceso.returncode != 0:
            raise RuntimeError(f"Fallo en FFmpeg durante la sincronización: {proceso.stderr}")
            
        return cmd