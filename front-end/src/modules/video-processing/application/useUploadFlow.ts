import { useCallback } from 'react';
import { useUploadVideo } from '@/modules/video-processing/application/useUploadVideo';
import type { UploadVideoResponseDto } from "@/modules/video-processing/domain/video.types";

interface UploadFlowInput {
  file: File;
  mode: "FACE_TRACKING" | "CENTER_CROP";
}

interface UploadFlowResult {
  uploadResponse: UploadVideoResponseDto;
  jobId: string;
}

export function useUploadFlow() {
  const { upload, isUploading, error, clearError } = useUploadVideo();

  const submitUpload = useCallback(
    async ({ file, mode }: UploadFlowInput): Promise<UploadFlowResult | null> => {
      try {
        const uploadResponse = await upload({ file, mode });
        return {
          uploadResponse,
          jobId: String(uploadResponse.id),
        };
      } catch {
        return null;
      }
    },
    [upload]
  );

  return {
    submitUpload,
    isUploading,
    error,
    clearError,
  };
}
