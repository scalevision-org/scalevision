import cv2
from ultralytics import YOLO

class VideoRecognizer:
    def __init__(self, model_path="yolov8n.pt"):
        self.model = YOLO(model_path)
    
    def scan_for_subjects(self, video_path, min_ratio=0.40, max_subjects=3):
        """
        Escanea el video con Tolerancia a Oclusión y Filtro de Escenario.
        Ignora a las personas en primer plano (público) y permite cortes de tracking.
        """
        print(f"INFO: [YOLO] Escaneando video con heurística de escenario (Min Ratio: {min_ratio*100}%)...")
        cap = cv2.VideoCapture(video_path)
        
        subject_counts = {}
        subject_sample_frames = {}
        subject_max_areas = {} 
        
        frame_idx = 0
        while cap.isOpened():
            ret, frame = cap.read()
            if not ret: break
            
            results = self.model.track(frame, classes=[0], tracker="bytetrack.yaml", persist=True, verbose=False)
            
            if results[0].boxes is not None and results[0].boxes.id is not None:
                ids = results[0].boxes.id.cpu().numpy().astype(int)
                boxes = results[0].boxes.xyxy.cpu().numpy()
                h, w = frame.shape[:2]
                
                for track_id, box in zip(ids, boxes):
                    x1, y1, x2, y2 = map(int, box)
                    
                    # 1. HEURÍSTICA DE ESCENARIO (Eliminar Ruido de Primer Plano)
                    # Si la parte superior de la persona (cabeza) empieza más abajo del 55% de la pantalla,
                    # asumimos que es público sentado o alguien asomándose, y lo ignoramos.
                    if y1 > h * 0.55:
                        continue
                        
                    # 2. Contar aparición
                    subject_counts[track_id] = subject_counts.get(track_id, 0) + 1
                    
                    # 3. Calcular el área para buscar el mejor momento de la foto
                    area = (x2 - x1) * (y2 - y1)
                    
                    # 4. Guardar miniatura con un Padding más amplio (20%) para ver el contexto
                    if track_id not in subject_max_areas or area > subject_max_areas[track_id]:
                        subject_max_areas[track_id] = area
                        
                        pad_x = int((x2 - x1) * 0.20)
                        pad_y = int((y2 - y1) * 0.20)
                        
                        px1 = max(0, x1 - pad_x)
                        py1 = max(0, y1 - pad_y)
                        px2 = min(w, x2 + pad_x)
                        py2 = min(h, y2 + pad_y)
                        
                        if py2 > py1 and px2 > px1:
                            subject_sample_frames[track_id] = frame[py1:py2, px1:px2].copy()
                            
            frame_idx += 1

        cap.release()
        
        valid_subjects = []
        if frame_idx == 0:
            return valid_subjects
            
        for track_id, count in subject_counts.items():
            ratio = count / frame_idx
            # Aplicamos el ratio más permisivo (40%)
            if ratio >= min_ratio:
                valid_subjects.append({
                    "numeric_id": track_id,
                    "ratio": ratio,
                    "thumbnail_img": subject_sample_frames.get(track_id)
                })
        
        # Ordenar por los que más aparecen y cortar al límite de 3
        valid_subjects = sorted(valid_subjects, key=lambda x: x["ratio"], reverse=True)[:max_subjects]
        
        print(f"INFO: [YOLO] {len(valid_subjects)} sujetos superaron el filtro espacial y de permanencia.")
        return valid_subjects
    