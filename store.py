import time
from typing import Dict, Any

# Mock Database
jobs_db: Dict[str, Any] = {}

def create_job(job_id: str):
    jobs_db[job_id] = {
        "id": job_id,
        #"created_at": time.time(),
        "scan_status": "SUBIDO",
        "process_status": None,
        "process_started_at": None,
        "subjects": ["person_1", "person_2"] # IDs simulados detectados
    }

def get_job(job_id: str):
    return jobs_db.get(str(job_id))

def update_job(job_id: str, data: Dict):
    if str(job_id) in jobs_db:
        jobs_db[str(job_id)].update(data)