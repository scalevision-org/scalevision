import cv2
from ultralytics import YOLO

class SubjectTracker:
    def __init__(self, model_path="yolov8n.pt"): # Ultralytics descargará el modelo automáticamente
        self.model = YOLO(model_path)

    def extract_trajectory(self, video_path, target_subject_id):
        """
        Ejecuta YOLO + ByteTrack y extrae los bounding boxes del sujeto seleccionado.
        """
        print(f"INFO: [ByteTrack] Extrayendo trayectoria para ID {target_subject_id}...")
        
        # Extraemos el ID numérico de ByteTrack desde el string del Frontend
        try:
            numeric_id = int(target_subject_id.split("_")[-1])
        except ValueError:
            numeric_id = 1

        cap = cv2.VideoCapture(video_path)
        fps = cap.get(cv2.CAP_PROP_FPS)
        total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
        
        trajectory = {} # Almacenará: frame_idx -> [x1, y1, x2, y2]
        frame_idx = 0
        
        while cap.isOpened():
            ret, frame = cap.read()
            if not ret: 
                break
            
            # persist=True activa el tracker interno de Ultralytics
            results = self.model.track(frame, classes=[0], tracker="bytetrack.yaml", persist=True, verbose=False)
            
            if results[0].boxes is not None and results[0].boxes.id is not None:
                boxes = results[0].boxes.xyxy.cpu().numpy()
                ids = results[0].boxes.id.cpu().numpy().astype(int)
                
                for box, track_id in zip(boxes, ids):
                    # Solo guardamos la trayectoria de la persona que nos interesa
                    if track_id == numeric_id:
                        trajectory[frame_idx] = box.tolist()
                        break
            
            frame_idx += 1
            
        cap.release()
        return trajectory, total_frames, fps