import { useCallback, useState } from "react";
import axios from "axios";
import {
  uploadVideo,
  type UploadVideoResponse,
} from "@/modules/video-processing/api/videoProcessing.api";

interface UploadVideoInput {
  file: File;
  mode: "FACE_TRACKING" | "CENTER_CROP";
}

export function useUploadVideo() {
  const [isUploading, setIsUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  const upload = useCallback(
    async ({ file, mode }: UploadVideoInput): Promise<UploadVideoResponse> => {
      setIsUploading(true);
      setError(null);
      console.info("[Upload] Enviando request", {
        endpoint: "/video",
        fileName: file.name,
        fileSizeBytes: file.size,
        modo_corte: mode,
      });

      try {
        const response = await uploadVideo({ file, mode });
        console.info("[Upload] Request exitosa", response);
        return response;
      } catch (error) {
        const apiMessage = axios.isAxiosError<{ message?: string }>(error)
          ? error.response?.data?.message
          : undefined;
        const message =
          typeof apiMessage === "string" && apiMessage.trim().length > 0
            ? apiMessage
            : "No se pudo subir el video al backend. Intenta nuevamente.";
        console.error("[Upload] Request fallida", error);
        setError(message);
        throw new Error(message);
      } finally {
        setIsUploading(false);
      }
    },
    [],
  );

  return {
    upload,
    isUploading,
    error,
    clearError,
  };
}