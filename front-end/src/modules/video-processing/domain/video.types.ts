export type VideoStatus =
  | "SUBIDO"
  | "PROCESANDO"
  | "PROCESADO"
  | "CORTAR"
  | "CORTANDO"
  | "CORTADO"
  | "ERROR";

export type VideoCutMode = "FACE_TRACKING" | "CENTER_CROP";

export interface VideoPoc {
  id: number;
  estado: VideoStatus;
  url_video_original: string;
  modo_corte: VideoCutMode;
}

export interface UploadVideoRequestDto {
  file: File;
  mode: VideoCutMode;
}

export interface UploadVideoResponseDto {
  id: number;
  url_video_original: string;
  estado: VideoStatus;
  modo_corte: VideoCutMode;
}

export interface VideoStatusResponseDto {
  id: number;
  estado: VideoStatus;
}

export interface VideoThumbnailsResponseDto {
  id: number;
  url_mini_vista_01: string;
  url_mini_vista_02: string;
  url_mini_vista_03: string;
}

export interface CutVideoRequestDto {
  mini_vista_id: string;
}

export interface VideoFinalResponseDto {
  id: number;
  url_video_final: string;
}
