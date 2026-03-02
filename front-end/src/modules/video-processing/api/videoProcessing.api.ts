import { httpClient } from "@/infrastructure/http/httpClient";
import type { VideoStatus } from "@/modules/video-processing/domain/video.types";

export interface UploadVideoPayload {
  file: File;
  mode: "FACE_TRACKING" | "CENTER_CROP";
}

export interface UploadVideoResponse {
  id: number;
  url_video_original: string;
  estado: VideoStatus;
}

export interface VideoStatusResponse {
  id: number;
  estado: VideoStatus;
}

export async function uploadVideo({
  file,
  mode,
}: UploadVideoPayload): Promise<UploadVideoResponse> {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("modo_corte", mode);

  const { data } = await httpClient.post<UploadVideoResponse>(
    "/video",
    formData,
  );

  return data;
}

export async function getVideoStatus(
  videoId: number | string,
): Promise<VideoStatusResponse> {
  const { data } = await httpClient.get<VideoStatusResponse>(
    `/videos/estado/${videoId}`,
  );

  return data;
}