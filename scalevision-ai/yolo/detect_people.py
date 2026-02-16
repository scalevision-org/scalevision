"""
Detección de personas en video usando YOLO26
ScaleVision AI - Módulo de detección de personas
"""

import cv2
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
    # Cargar el modelo YOLO preentrenado
    print(f"Cargando modelo YOLO: {model_path}")
    model = YOLO(model_path)
    
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
        
        # Realizar detección con YOLO
        # class 0 = persona en COCO dataset
        results = model(frame, classes=[0], verbose=False)
        
        # Procesar resultados
        for result in results:
            # Obtener las cajas delimitadoras
            boxes = result.boxes
            
            for box in boxes:
                # Obtener coordenadas
                x1, y1, x2, y2 = box.xyxy[0].cpu().numpy()
                x1, y1, x2, y2 = int(x1), int(y1), int(x2), int(y2)
                
                # Obtener confianza
                confidence = float(box.conf[0])
                
                # Dibujar bounding box
                cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
                
                # Agregar etiqueta con confianza
                label = f"Persona {confidence:.2f}"
                cv2.putText(
                    frame,
                    label,
                    (x1, y1 - 10),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.5,
                    (0, 255, 0),
                    2
                )
        
        # Mostrar contador de personas detectadas
        person_count = len(results[0].boxes)
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
    print("=" * 50)
    print("ScaleVision AI - Detección de Personas con YOLO")
    print("=" * 50)
    
    # Ejecutar detección con optimizaciones de rendimiento
    # Para mejorar velocidad, puedes usar:
    # - skip_frames=2 (procesa 1 de cada 3 frames)
    # - resize_width=640 (redimensiona el video)
    # - model_path="yolo26n.pt" (modelo más ligero, ya configurado por defecto)
    
    detect_people_in_video(
        skip_frames=0,      # Procesa 1 de cada 2 frames (más rápido)
        resize_width=640    # Redimensiona a 640px de ancho
    )
