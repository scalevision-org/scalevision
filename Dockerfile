# 1. Usar una imagen base oficial de Python (ligera)
FROM python:3.10-slim

# 2. Evitar que Python genere archivos .pyc y forzar logs en tiempo real
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# 3. Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# 4. Instalar dependencias del Sistema Operativo críticas para IA y Video
# OpenCV requiere libgl1 y libglib2.0-0. Nuestro core requiere ffmpeg.
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# 5. Copiar e instalar dependencias de Python
COPY requirements.txt /app/
RUN pip install --no-cache-dir -r requirements.txt

# 6. Copiar el resto del código fuente al contenedor
COPY . /app/

# 7. Crear los directorios de almacenamiento temporal para evitar errores de I/O
RUN mkdir -p /app/scalevision_data/originals \
    /app/scalevision_data/processed \
    /app/scalevision_data/thumbnails \
    /app/scalevision_data/json_logs

# 8. Exponer el puerto de FastAPI
EXPOSE 8000

# 9. Comando de arranque del servidor (Sin --reload para producción)
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]