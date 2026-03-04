import os

# Definimos la estructura de carpetas local que simula el bucket de OCI
BASE_DIR = "scalevision_data"
DIRECTORIES = {
    "originals": os.path.join(BASE_DIR, "originals"),
    "processed": os.path.join(BASE_DIR, "finals"),
    "thumbnails": os.path.join(BASE_DIR, "thumbnails"),
    "json_logs": os.path.join(BASE_DIR, "json_logs"),
}


def init_storage():
    print("\n--- Verificando Infraestructura de Almacenamiento ---")
    for name, path in DIRECTORIES.items():
        os.makedirs(path, exist_ok=True)
        print(f"OK: Directorio listo -> {path}")
    print("---------------------------------------------------\n")


def get_path(folder_key, filename):
    return os.path.join(DIRECTORIES[folder_key], filename)
