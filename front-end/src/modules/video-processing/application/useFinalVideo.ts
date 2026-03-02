import { useQuery } from "@tanstack/react-query";
import { getFinalVideo } from "@/modules/video-processing/api/videoProcessing.api";
import type { VideoFinalResponseDto } from "@/modules/video-processing/domain/video.types";

interface UseFinalVideoOptions {
  videoId?: number | string;
  enabled?: boolean;
}

export function useFinalVideo({ videoId, enabled = true }: UseFinalVideoOptions) {
  return useQuery<VideoFinalResponseDto, unknown>({
    queryKey: ["video-final", videoId],
    queryFn: async () => {
      if (!videoId) {
        throw new Error("VideoId requerido");
      }

      return getFinalVideo(videoId);
    },
    enabled: enabled && Boolean(videoId),
  });
}
