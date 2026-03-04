# DOCUMENTACIÓN: Integración OCI Object Storage para Backend de Videos

**Fecha:** 14 de febrero de 2026  
**Proyecto:** Sistema de Gestión de Videos con Procesamiento Automático  
**Entorno:** Development

---

## 1. INFORMACIÓN DEL BUCKET

| Parámetro | Valor |
|-----------|-------|
| **Nombre del Bucket** | `videos-dev-storage` |
| **Región** | Mexico Central (Queretaro) |
| **Código de Región** | `mx-queretaro-1` |
| **Namespace** | `axawa14hxvi3` |
| **Visibilidad** | Privado (requiere autenticación) |
| **Nivel de Almacenamiento** | Estándar |
| **Compartimento** | `edwin10 (raíz)` |

---

## 2. CONFIGURACIÓN DE IAM

### Grupo Creado
- **Nombre:** `video-storage-group`
- **Descripción:** Grupo con permisos para gestionar el bucket de videos
- **OCID:** `ocid1.group.oc1..aaaaaaaabcmqxjvij23igvlhq27s4icxcuhgo2ysqlsezlrr3on3bn7sesoa`

### Usuario de Servicio
- **Nombre:** `video-storage-user`
- **Username:** `user`
- **Email:** `edwinhuevo10@gmail.com`
- **OCID Usuario:** `ocid1.user.oc1..aaaaaaaaj2k2x2b36vpi4sbida3cdop6xkaarpjrvpv3tromjf2jr5hdf7ba`
- **Grupo asignado:** `video-storage-group`

### Políticas Aplicadas
```
Allow group video-storage-group to manage objects in tenancy where target.bucket.name='videos-dev-storage'
Allow group video-storage-group to read buckets in tenancy
```

**Permisos otorgados:**
- ✅ Crear objetos (subir videos)
- ✅ Leer objetos (descargar videos)
- ✅ Eliminar objetos
- ✅ Actualizar objetos
- ✅ Listar objetos del bucket

---

## 3. CREDENCIALES DE ACCESO

### Información de Tenancy
- **Tenancy OCID:** `ocid1.tenancy.oc1..aaaaaaaaxgjsx5f4q55zvh7oy2pkgsaehuncagvek2i3syxsrptw6idewzwq`
- **Tenancy Name:** `edwin10`

### Auth Token
- **Descripción:** `backend-video-storage-token`
- **Token:** `[GUARDADO DE FORMA SEGURA - NO EXPONER EN DOCUMENTACIÓN]`

### ⚠️ IMPORTANTE:
- El Auth Token solo se mostró una vez al generarlo
- Debe almacenarse como **variable de entorno** en tu sistema
- **NUNCA** commitear el token en Git o repositorios
- Si lo perdiste, deberás eliminar el token actual y generar uno nuevo

### Cómo compartir el Token de forma segura

**Opciones recomendadas:**

1. **Variables de entorno (desarrollo local):**
   ```env
   OCI_AUTH_TOKEN=tu_token_aqui
   ```
   - Crear archivo `.env` en la raíz del proyecto
   - Agregar `.env` al `.gitignore`
   - Compartir el token por canal seguro (Slack DM, gestor de contraseñas)

2. **Gestores de contraseñas del equipo:**
   - 1Password (vault compartido)
   - LastPass (carpeta compartida)
   - Bitwarden (organización)

3. **Secrets en producción:**
   - Kubernetes Secrets
   - Docker Secrets
   - AWS Secrets Manager / Azure Key Vault

### Endpoint de Object Storage
- **Endpoint:** `https://objectstorage.mx-queretaro-1.oraclecloud.com`
- **Namespace Endpoint:** `https://axawa14hxvi3.objectstorage.mx-queretaro-1.oci.customer-oci.com`

### Uso del Auth Token

**En desarrollo local (.env):**
```env
OCI_AUTH_TOKEN=tu_token_aqui
```

**En Spring Boot (application.yml):**
```yaml
oci:
  auth-token: ${OCI_AUTH_TOKEN}
```

**En FastAPI (Python):**
```python
import os
auth_token = os.getenv('OCI_AUTH_TOKEN')
```

---

## 4. NAMING CONVENTION RECOMENDADA

### Estructura de Carpetas

```
videos-dev-storage/
├── original/
│   ├── {userId}/
│   │   └── {videoId}_{timestamp}.mp4
│   └── ...
├── processed/
│   ├── {userId}/
│   │   └── {videoId}_vertical_{timestamp}.mp4
│   └── ...
└── thumbnails/
    ├── {userId}/
    │   └── {videoId}_thumb.jpg
    └── ...
```

### Ejemplos de Object Keys

**Video Original:**
```
original/user123/vid_abc456_20260214_103045.mp4
```

**Video Procesado:**
```
processed/user123/vid_abc456_vertical_20260214_104512.mp4
```

**Thumbnail:**
```
thumbnails/user123/vid_abc456_thumb.jpg
```

### Formato de Timestamp
```
YYYYMMDD_HHMMSS
Ejemplo: 20260214_103045
```

---

## 5. INTEGRACIÓN CON SPRING BOOT (JAVA)

### 5.1 Dependencias Maven

Agrega esto a tu `pom.xml`:

```xml
<dependencies>
    <!-- OCI SDK -->
    <dependency>
        <groupId>com.oracle.oci.sdk</groupId>
        <artifactId>oci-java-sdk-objectstorage</artifactId>
        <version>3.39.0</version>
    </dependency>
    
    <dependency>
        <groupId>com.oracle.oci.sdk</groupId>
        <artifactId>oci-java-sdk-common</artifactId>
        <version>3.39.0</version>
    </dependency>
</dependencies>
```

### 5.2 Configuración application.yml

```yaml
oci:
  tenancy-id: ocid1.tenancy.oc1..aaaaaaaaxgjsx5f4q55zvh7oy2pkgsaehuncagvek2i3syxsrptw6idewzwq
  user-id: ocid1.user.oc1..aaaaaaaaj2k2x2b36vpi4sbida3cdop6xkaarpjrvpv3tromjf2jr5hdf7ba
  region: mx-queretaro-1
  namespace: axawa14hxvi3
  bucket-name: videos-dev-storage
  auth-token: ${OCI_AUTH_TOKEN} # Variable de entorno

objectstorage:
  endpoint: https://objectstorage.mx-queretaro-1.oraclecloud.com

fastapi:
  base-url: ${FASTAPI_BASE_URL:http://localhost:8000}
  timeout-seconds: 30
```

---

## 6. INTEGRACIÓN CON FASTAPI (PYTHON)

### 6.1 Instalación de Dependencias

```bash
pip install oci fastapi uvicorn python-multipart opencv-python --break-system-packages
```

### 6.2 Archivo de Configuración (.env)

```env
OCI_TENANCY_ID=ocid1.tenancy.oc1..aaaaaaaaxgjsx5f4q55zvh7oy2pkgsaehuncagvek2i3syxsrptw6idewzwq
OCI_USER_ID=ocid1.user.oc1..aaaaaaaaj2k2x2b36vpi4sbida3cdop6xkaarpjrvpv3tromjf2jr5hdf7ba
OCI_REGION=mx-queretaro-1
OCI_NAMESPACE=axawa14hxvi3
OCI_BUCKET_NAME=videos-dev-storage
OCI_AUTH_TOKEN=tu_auth_token_aqui
```

---

## 7. FLUJO COMPLETO DE INTEGRACIÓN

### Descripción del Flujo

Este flujo describe el proceso completo desde que el usuario sube un video hasta que visualiza el resultado procesado.

### Paso a Paso

**1. Usuario sube video desde el Frontend**
- El cliente selecciona un archivo de video (video.mp4) desde su dispositivo
- Frontend envía el video mediante POST request a Spring Boot

**2. Spring Boot Backend recibe el video**
- Endpoint: `POST /api/videos/upload`
- Recibe MultipartFile del frontend

**3. Backend usa oci-java-sdk-objectstorage para:**
- Subir video a OCI Storage mediante `objectStorage.putObject(...)`
- Generar Pre-Authenticated Request (PAR) URL válida por 2 horas mediante `objectStorage.createPreauthenticatedRequest(...)`
- La PAR URL permite acceso temporal sin autenticación

**4. Backend guarda metadata en Base de Datos H2:**

```
Video (entity)
─────────────────────────────
• id: UUID
• userId: String
• originalObjectKey: String
• originalParUrl: String  ← PAR URL generada
• status: "pending"
• createdAt: Timestamp
```

**5. Backend envía a FastAPI:**
- Endpoint: `POST http://fastapi:8000/ai/process-video`
- Body:
```json
{
  "video_url": "https://...PAR_URL...",
  "callback_url": "https://backend/webhook"
}
```

**6. Backend responde al Frontend:**
```json
{
  "status": "processing",
  "videoId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**7. FastAPI descarga, procesa y sube el video:**
- FastAPI inicia procesamiento asíncrono

**8. FastAPI descarga video desde PAR URL**
- Utiliza la URL temporal para descargar sin autenticación adicional

**9. FastAPI procesa el video:**
- Detecta rostro o sujeto principal
- Recorta video a formato vertical (9:16)
- Aplica estabilización si es necesario

**10. FastAPI sube video procesado a OCI:**
- Object Key: `processed/user123/vid_vertical_timestamp.mp4`
- Utiliza credenciales de OCI Python SDK

**11. FastAPI envía callback a Backend:**
- Endpoint: `POST https://backend/webhook`
- Body:
```json
{
  "status": "completed",
  "processed_object_key": "processed/user123/vid_vertical_20260214_104512.mp4"
}
```

**12. Backend recibe webhook de FastAPI**
- Valida que el webhook sea legítimo
- Procesa la respuesta

**13. Backend usa oci-java-sdk-objectstorage para:**
- Generar nueva PAR URL del video procesado
- `objectStorage.createPreauthenticatedRequest(...)`

**14. Backend actualiza Base de Datos H2:**

```
Video (entity) - Actualización
─────────────────────────────
• processedObjectKey: String
• processedParUrl: String ← Nueva PAR URL
• status: "completed"
• updatedAt: Timestamp
```

**15. Backend notifica al Frontend:**
- Mediante WebSocket, Server-Sent Events o Polling
- Envía URL del video procesado

**16. Frontend muestra video procesado:**
- Usuario visualiza el video en formato vertical
- Puede comparar con el original
- Puede descargar o compartir el resultado

---

### Diagrama de Secuencia Simplificado

```
Frontend → Backend: Upload video.mp4

Backend → OCI Storage: PUT video (original/)
Backend → OCI Storage: Generate PAR URL
Backend → Database H2: Save metadata + PAR URL
Backend → FastAPI: POST /ai/process-video {par_url}
Backend → Frontend: Response {status: "processing"}

FastAPI → OCI Storage: Download video (PAR URL)
FastAPI: Process video (detect + crop)
FastAPI → OCI Storage: PUT processed video (processed/)
FastAPI → Backend: POST /webhook {processed_object_key}

Backend → OCI Storage: Generate PAR URL (processed video)
Backend → Database H2: Update metadata + new PAR URL
Backend → Frontend: Notify {status: "completed", url}

Frontend: Display processed video
```

---

### Modelo de Datos (Base de Datos H2)

```java
@Entity
@Table(name = "videos")
public class Video {
    @Id
    private UUID id;
    
    private String userId;
    
    // Video original
    private String originalObjectKey;  // "original/user123/vid_abc456_20260214_103045.mp4"
    private String originalParUrl;     // "https://axawa14hxvi3.objectstorage..."
    
    // Video procesado
    private String processedObjectKey; // "processed/user123/vid_abc456_vertical_20260214_104512.mp4"
    private String processedParUrl;    // "https://axawa14hxvi3.objectstorage..."
    
    // Estado y timestamps
    private String status;             // "pending", "processing", "completed", "failed"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

## 8. GESTIÓN DE COSTOS Y LÍMITES

### Estado Actual
- **Uso actual:** ~1.2 MiB de almacenamiento (Object Storage + Archive Storage)
- **Tipo de cuenta:** Prueba gratuita con almacenamiento ilimitado

### Límites de Free Tier

**Durante la Prueba Gratuita:**
- Almacenamiento ilimitado de objetos y archivos
- Sin restricciones de transferencia de datos

**Después de la Prueba Gratuita (cuenta Always Free):**
- **10 GiB** de Object Storage gratis en región principal
- **10 GiB** de Archive Storage gratis en región principal
- **Límite total:** 20 GiB combinados de almacenamiento
- **10 GiB** de transferencia de salida gratis al mes

### ⚠️ Advertencia Importante

Cuando la prueba gratuita finalice, la cuenta se convertirá automáticamente a **Always Free**. Si el uso total supera los **20 GiB**, los datos serán **eliminados automáticamente**.

**Recomendación:** Reducir el uso a menos de 20 GiB antes de que finalice el período de prueba para evitar pérdida de datos.

### Costos Estimados (Después de Free Tier)

| Recurso | Precio Aproximado |
|---------|-------------------|
| Almacenamiento | $0.0255 USD/GB/mes |
| Transferencia de datos (egress) | $0.0085 USD/GB |
| Solicitudes PUT/POST | $0.04 USD/10,000 requests |
| Solicitudes GET | $0.004 USD/10,000 requests |

### Estrategias de Optimización

1. **Implementar Lifecycle Policies**
   - Mover videos antiguos a Archive Storage después de 30 días
   - Eliminar videos procesados después de 90 días
   - Configurar reglas automáticas de limpieza

2. **Compresión de Videos**
   - Usar códec H.265 (HEVC) en lugar de H.264
   - Reducir bitrate sin pérdida notable de calidad
   - Ajustar resolución según necesidades (720p vs 1080p)

3. **Gestión de URLs PAR**
   - Cachear URLs pre-firmadas para reducir solicitudes
   - Configurar TTL adecuado (2-4 horas)
   - Usar CDN para reducir transferencia directa desde OCI

4. **Monitoreo de Uso**
   - Revisar métricas de almacenamiento semanalmente
   - Configurar alertas cuando se acerque al límite de 15 GiB
   - El uso mostrado puede tener cierta demora respecto al uso real

---

## 9. PRUEBAS Y VALIDACIÓN

### Checklist de Validación OCI

- [x] Bucket creado correctamente
- [x] Usuario con permisos configurado
- [x] Grupo asignado al usuario
- [x] Políticas aplicadas y activas
- [x] Auth Token generado y guardado
- [x] Prueba de carga manual exitosa
- [x] Prueba de descarga manual exitosa

### Cómo verificar las pruebas

#### Verificar que el video se subió:

1. Ve a la consola de OCI
2. Navega a: **Storage** → **Buckets** → **videos-dev-storage**
3. Haz clic en la pestaña **"Objetos"**
4. Verifica que aparezca el archivo `fast_and_furious.mp4`

**Evidencia:**
- ✅ Nombre del archivo visible
- ✅ Tamaño: 1.2 MiB
- ✅ Fecha de subida: 13 feb 2026
- ✅ Estado: Activo

#### Verificar que el video se descargó:

1. Revisa tu carpeta de **Descargas**
2. Busca el archivo `fast_and_furious.mp4`
3. Reproduce el video para verificar que no esté corrupto

## 10. RECURSOS ADICIONALES

### Documentación Oficial
- [OCI Object Storage Documentation](https://docs.oracle.com/en-us/iaas/Content/Object/home.htm)
- [OCI Pre-Authenticated Requests](https://docs.oracle.com/en-us/iaas/Content/Object/Tasks/usingpreauthenticatedrequests.htm)
- [OCI Java SDK](https://docs.oracle.com/en-us/iaas/tools/java/latest/)
- [OCI Python SDK](https://oracle-cloud-infrastructure-python-sdk.readthedocs.io/)

### Ejemplos de Código
- [GitHub: OCI Java Examples](https://github.com/oracle/oci-java-sdk/tree/master/bmc-examples)
- [GitHub: OCI Python Examples](https://github.com/oracle/oci-python-sdk/tree/master/examples)

