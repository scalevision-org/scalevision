## Seguridad — `validated_domains`

### ¿Qué significa `blob`?

En este contrato, el valor `blob` hace referencia a **Azure Blob Storage**, el servicio de almacenamiento de archivos de Microsoft Azure.

**BLOB** significa **Binary Large Object**, y se utiliza para almacenar archivos como videos, imágenes y otros objetos binarios.

---

### Propósito de `validated_domains`

El campo `validated_domains` define una **lista blanca (whitelist)** de dominios de almacenamiento autorizados desde los cuales el servicio de IA puede descargar videos para su procesamiento.

Esto es una medida de seguridad para evitar que el sistema procese contenido proveniente de fuentes no confiables.

Ejemplo dentro del contrato:

```json
"validated_domains": ["objectstorage", "blob"]
```

### Valores soportados
| Valor           | Proveedor                         | Servicio           |
| --------------- | --------------------------------- | ------------------ |
| `objectstorage` | Oracle Cloud Infrastructure (OCI) | Object Storage     |
| `blob`          | Microsoft Azure                   | Azure Blob Storage |

## Ejemplos de URLs válidas

### OCI Object Storage

```
https://objectstorage.<region>.oraclecloud.com/...
```

### Azure Blob Storage
```
https://<account>.blob.core.windows.net/...
```

## Reglas de validación aplicadas

Cuando el backend recibe un video_url, se valida que:

- La URL utilice protocolo HTTPS

- El dominio pertenezzca a un proveedor autorizado

- El enlace tenga una expiración suficiente para completar el procesamiento

## Riesgos mitigados

Esta validación protege contra:

- SSRF (Server-Side Request Forgery)

- Acceso a recursos internos del servidor

- Descarga de contenido malicioso

- Uso indebido de infraestructura de procesamiento

- Exposición accidental de datos


## Nota de implementación

Si el proyecto utiliza exclusivamente OCI Object Storage, el valor blob puede eliminarse de la lista:

```
"validated_domains": ["objectstorage"]
```

## Resumen

- blob se refiere a Azure Blob Storage

- Se utiliza como mecanismo de control de seguridad

- No afecta la lógica del procesamiento de IA

- Es opcional dependiendo del proveedor cloud utilizado