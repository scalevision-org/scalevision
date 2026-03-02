import { useMutation } from "@tanstack/react-query";
import { cutVideo } from "@/modules/video-processing/api/videoProcessing.api";
import type { CutVideoRequestDto } from "@/modules/video-processing/domain/video.types";

interface CutVideoInput {
  videoId: number | string;
  payload: CutVideoRequestDto;
}

export function useCutVideo() {
  return useMutation({
    mutationFn: async ({ videoId, payload }: CutVideoInput) => {
      return cutVideo(videoId, payload);
    },
  });
}
