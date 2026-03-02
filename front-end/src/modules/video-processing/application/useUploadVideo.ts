import { useCallback, useMemo } from "react";
import { useMutation } from "@tanstack/react-query";
import axios from "axios";
import { uploadVideo } from "@/modules/video-processing/api/videoProcessing.api";
import type {
  UploadVideoRequestDto,
  UploadVideoResponseDto,
} from "@/modules/video-processing/domain/video.types";

type UploadVideoInput = UploadVideoRequestDto;

export function useUploadVideo() {
  const mutation = useMutation<
    UploadVideoResponseDto,
    unknown,
    UploadVideoInput
  >({
    mutationFn: async ({ file, mode }) => {
      console.info("[Upload] Enviando request", {
        endpoint: "/videos/subir",
        fileName: file.name,
        fileSizeBytes: file.size,
        modo_corte: mode,
      });
      const response = await uploadVideo({ file, mode });
      console.info("[Upload] Request exitosa", response);
      return response;
    },
  });

  const error = useMemo(() => {
    if (!mutation.error) {
      return null;
    }

    const apiMessage = axios.isAxiosError<{ message?: string }>(mutation.error)
      ? mutation.error.response?.data?.message
      : undefined;
    return typeof apiMessage === "string" && apiMessage.trim().length > 0
      ? apiMessage
      : "No se pudo subir el video al backend. Intenta nuevamente.";
  }, [mutation.error]);

  const clearError = useCallback(() => {
    mutation.reset();
  }, [mutation]);

  return {
    upload: mutation.mutateAsync,
    isUploading: mutation.isPending,
    error,
    clearError,
  };
}
