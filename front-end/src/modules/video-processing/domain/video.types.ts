export type VideoStatus =
  | "SUBIDO"
  | "PROCESANDO"
  | "PROCESADO"
  | "CORTAR"
  | "CORTANDO"
  | "CORTADO"
  | "ERROR";

export interface VideoPoc {
  id: number;
  estado: VideoStatus;
  url_video_original: string;
}
