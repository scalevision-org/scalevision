import urllib.request
import requests
import os

class CloudIntegration:
    def __init__(self):
        # Configuraciones de OCI desde variables de entorno
        self.namespace = os.getenv('OCI_NAMESPACE', 'axawa14hxvi3')
        self.bucket = os.getenv('OCI_BUCKET_NAME', 'videos-dev-storage')

    def download_video_from_par(self, par_url, local_destination):
        """Descarga el video fuente usando la URL Pre-Autenticada (PAR) de OCI."""
        print(f"[CLOUD] Descargando video desde OCI PAR URL...")
        try:
            urllib.request.urlretrieve(par_url, local_destination)
            print(f"[CLOUD] Descarga completada: {local_destination}") #CARPETA scalevision_data/originals
            return True
        except Exception as e:
            print(f"[CLOUD ERROR] Fallo al descargar de OCI: {str(e)}")
            return False
        
        

    def send_webhook_callback(self, callback_url, webhook_secret, payload):
        """Envía el resultado asíncrono de vuelta al Orquestador (Spring Boot)."""
        print(f"[WEBHOOK] Notificando al Backend en {callback_url}...")
        
        headers = {
            "Content-Type": "application/json",
            "X-AI-Schema-Version": "1.0.0",
            "X-Webhook-Secret": webhook_secret
        }
        
        try:
            response = requests.post(callback_url, json=payload, headers=headers, timeout=10)
            response.raise_for_status()
            print("[WEBHOOK] Notificación enviada exitosamente (200 OK).")
        except requests.exceptions.RequestException as e:
            # En producción se sugiere implementar lógica de reintentos (backoff exponencial)
            print(f"[WEBHOOK ERROR] Fallo de red al contactar al Backend: {str(e)}")