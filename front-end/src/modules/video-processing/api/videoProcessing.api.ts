import { httpClient } from "@/infrastructure/http/httpClient";
import type {
  CutVideoRequestDto,
  UploadVideoRequestDto,
  UploadVideoResponseDto,
  VideoFinalResponseDto,
  VideoStatusResponseDto,
  VideoThumbnailsResponseDto,
} from "@/modules/video-processing/domain/video.types";


export async function uploadVideo({
  file,
  mode,
}: UploadVideoRequestDto): Promise<UploadVideoResponseDto> {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("modo_corte", mode);
  const { data } = await httpClient.post<UploadVideoResponseDto>(
    "/videos/subir",
    formData,
  );
  return data;
}

export async function getVideoStatus(
  videoId: number | string,
): Promise<VideoStatusResponseDto> {
  const { data } = await httpClient.get<VideoStatusResponseDto>(
    `/videos/estado/${videoId}`,
  );
  return data;
}

export async function getVideoThumbnails(
  videoId: number | string,
): Promise<VideoThumbnailsResponseDto> {
  const { data } = await httpClient.get<VideoThumbnailsResponseDto>(
    `/videos/mini-vistas/${videoId}`,
  );
  return data;
}

export async function cutVideo(
  videoId: number | string,
  payload: CutVideoRequestDto,
): Promise<{ ok: boolean }> {
  const { data } = await httpClient.post<{ ok: boolean }>(
    `/videos/cortar-video/${videoId}`,
    payload,
  );
  return data;
}

export async function getFinalVideo(
  videoId: number | string,
): Promise<VideoFinalResponseDto> {
  const { data } = await httpClient.get<VideoFinalResponseDto>(
    `/videos/final/${videoId}`,
  );
  return data;
}