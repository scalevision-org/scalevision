import { useQuery } from "@tanstack/react-query";
import { getVideoThumbnails } from "@/modules/video-processing/api/videoProcessing.api";
import type { VideoThumbnailsResponseDto } from "@/modules/video-processing/domain/video.types";

interface UseVideoThumbnailsOptions {
  videoId?: number | string;
  enabled?: boolean;
}

export function useVideoThumbnails({
  videoId,
  enabled = true,
}: UseVideoThumbnailsOptions) {
  return useQuery<VideoThumbnailsResponseDto, unknown>({
    queryKey: ["video-thumbnails", videoId],
    queryFn: async () => {
      if (!videoId) {
        throw new Error("VideoId requerido");
      }

      return getVideoThumbnails(videoId);
    },
    enabled: enabled && Boolean(videoId),
  });
}
