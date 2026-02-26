import { httpClient } from "@/infrastructure/http/httpClient";

export interface UploadVideoPayload {
  file: File;
  nickname?: string;
  duration?: number;
}

export interface UploadVideoResponse {
  id: number;
  url_video_original: string;
  estado: string;
}

export async function uploadVideo({
  file,
  nickname,
  duration,
}: UploadVideoPayload): Promise<UploadVideoResponse> {
  const formData = new FormData();
  formData.append("file", file);

  if (nickname) {
    formData.append("nickname", nickname);
  }

  if (typeof duration === "number") {
    formData.append("duration", String(duration));
  }

  const { data } = await httpClient.post<UploadVideoResponse>(
    "/videos",
    formData
  );

  return data;
}
