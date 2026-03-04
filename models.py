from pydantic import BaseModel, HttpUrl, Field
from typing import Optional, List, Literal
from uuid import UUID

# Phase 1: Scan
class ScanRequest(BaseModel):
    id: UUID
    video_url: HttpUrl
    modo_corte: Optional[Literal["face_tracking", "center_crop"]] = "face_tracking"

# Phase 2: Process
class ProcessRequest(BaseModel):
    id: UUID
    target_subject_id: Optional[str] = None
    strategy: Literal["face_tracking", "center_crop"]

# Common Metadata
class ScanMetadata(BaseModel):
    processing_time_ms: int
    video_duration_seconds: float
    frame_count: int

class Subject(BaseModel):
    subject_id: str
    thumbnail_url: str
    expires_in_seconds: int = 900

class Fallback(BaseModel):
    is_active: bool
    strategy: Optional[Literal["CENTER_CROP"]] = None
    reason: Optional[str] = None