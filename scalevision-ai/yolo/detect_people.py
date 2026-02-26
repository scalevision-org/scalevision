"""
Detección y tracking de personas en video usando YOLO26 con tracking integrado
ScaleVision AI - Módulo de detección de personas
"""

import cv2
import numpy as np
from ultralytics import YOLO


def detect_people_in_video(video_path="video.mp4", model_path="yolo26n.pt", skip_frames=0, resize_width=None):
    """
    Detecta personas en un video usando YOLO26
    
    Args:
        video_path (str): Ruta al archivo de video
        model_path (str): Ruta al modelo YOLO26 (se descargará automáticamente si no existe)
        skip_frames (int): Número de frames a saltar entre detecciones (0 = procesar todos)
        resize_width (int): Ancho para redimensionar el video (None = tamaño original)
    """
    # Cargar el modelo YOLO preentrenado con tracking integrado
    print(f"Cargando modelo YOLO26 con tracking: {model_path}")
    model = YOLO(model_path)
    print("Tracking ByteTrack activado (integrado en YOLO)")
    
    # Abrir el video
    cap = cv2.VideoCapture(video_path)
    
    if not cap.isOpened():
        print(f"Error: No se pudo abrir el video '{video_path}'")
        print("Asegúrate de que el archivo exista en el directorio actual")
        return
    
    # Obtener información del video
    fps = int(cap.get(cv2.CAP_PROP_FPS))
    width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    
    print(f"Video cargado: {width}x{height} @ {fps} FPS")
    print("Controles:")
    print("  - Presiona 'q' para salir")
    print("  - Presiona 'ESPACIO' para pausar/reanudar")
    
    frame_count = 0
    paused = False
    
    while True:
        # Leer frame del video
        ret, frame = cap.read()
        
        if not ret:
            print("Fin del video o error al leer frame")
            break
        
        frame_count += 1
        
        # Saltar frames si está configurado (para mejorar rendimiento)
        if skip_frames > 0 and frame_count % (skip_frames + 1) != 0:
            continue
        
        # Redimensionar frame si está configurado (para mejorar rendimiento)
        if resize_width is not None:
            aspect_ratio = frame.shape[0] / frame.shape[1]
            new_height = int(resize_width * aspect_ratio)
            frame = cv2.resize(frame, (resize_width, new_height))
        
        # Realizar detección y tracking con YOLO26 (ByteTrack integrado)
        # class 0 = persona en COCO dataset
        # persist=True activa el tracking con IDs persistentes
        results = model.track(frame, classes=[0], verbose=False, persist=True, tracker="bytetrack.yaml")
        
        # Procesar resultados con tracking
        person_count = 0
        
        if results[0].boxes is not None and len(results[0].boxes) > 0:
            boxes = results[0].boxes
            person_count = len(boxes)
            
            for box in boxes:
                # Obtener coordenadas
                x1, y1, x2, y2 = box.xyxy[0].cpu().numpy()
                x1, y1, x2, y2 = int(x1), int(y1), int(x2), int(y2)
                
                # Obtener confianza
                confidence = float(box.conf[0])
                
                # Obtener ID de tracking (si existe)
                if box.id is not None:
                    track_id = int(box.id[0])
                    label = f"ID:{track_id} {confidence:.2f}"
                else:
                    label = f"Persona {confidence:.2f}"
                
                # Dibujar bounding box
                cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
                
                # Agregar etiqueta
                cv2.putText(
                    frame,
                    label,
                    (x1, y1 - 10),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.5,
                    (0, 255, 0),
                    2
                )
        cv2.putText(
            frame,
            f"Personas detectadas: {person_count}",
            (10, 30),
            cv2.FONT_HERSHEY_SIMPLEX,
            1,
            (0, 255, 255),
            2
        )
        
        # Redimensionar frame para visualización (mitad del tamaño)
        h, w = frame.shape[:2]
        display_frame = cv2.resize(frame, (w // 2, h // 2))
        
        # Mostrar frame
        cv2.imshow("YOLO26 - Detección de Personas", display_frame)
        
        # Detectar teclas presionadas
        key = cv2.waitKey(1) & 0xFF
        
        # Pausar/reanudar con barra espaciadora
        if key == ord(' '):
            paused = not paused
            if paused:
                print("\n⏸️  VIDEO PAUSADO - Presiona ESPACIO para continuar")
            else:
                print("▶️  VIDEO REANUDADO")
        
        # Salir al presionar 'q'
        if key == ord('q'):
            print(f"\nDetección finalizada. Frames procesados: {frame_count}")
            break
        
        # Si está pausado, esperar hasta que se reanude
        while paused:
            key = cv2.waitKey(100) & 0xFF
            if key == ord(' '):
                paused = False
                print("▶️  VIDEO REANUDADO")
            elif key == ord('q'):
                print(f"\nDetección finalizada. Frames procesados: {frame_count}")
                cap.release()
                cv2.destroyAllWindows()
                return
    
    # Liberar recursos
    cap.release()
    cv2.destroyAllWindows()
    print("Recursos liberados correctamente")


if __name__ == "__main__":
    print("=" * 60)
    print("ScaleVision AI - Detección y Tracking de Personas")
    print("YOLO26 + ByteTrack")
    print("=" * 60)
    
    # Ejecutar detección con optimizaciones de rendimiento
    # Para mejorar velocidad, puedes usar:
    # - skip_frames=2 (procesa 1 de cada 3 frames)
    # - resize_width=640 (redimensiona el video)
    # - model_path="yolo26n.pt" (modelo más ligero, ya configurado por defecto)
    
    detect_people_in_video(
        skip_frames=0,      # Procesa todos los frames (0 = sin saltar)
        resize_width=640    # Redimensiona a 640px de ancho
    )
