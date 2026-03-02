import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import axios from "axios";
import { getVideoStatus } from "@/modules/video-processing/api/videoProcessing.api";
import type {
  VideoStatus,
  VideoStatusResponseDto,
} from "@/modules/video-processing/domain/video.types";

interface UseVideoStatusOptions {
  videoId?: number | string;
  enabled?: boolean;
  intervalMs?: number;
  stopAtProcessed?: boolean;
}

const TERMINAL_STATUSES: VideoStatus[] = ["CORTADO", "ERROR"];

export function useVideoStatus({
  videoId,
  enabled = true,
  intervalMs = 3000,
  stopAtProcessed = false,
}: UseVideoStatusOptions) {
  const query = useQuery<VideoStatusResponseDto, unknown>({
    queryKey: ["video-status", videoId],
    queryFn: async () => {
      if (!videoId) {
        throw new Error("VideoId requerido");
      }

      console.info("[Status] Enviando request", {
        endpoint: `/videos/estado/${videoId}`,
        videoId,
      });
      const response = await getVideoStatus(videoId);
      console.info("[Status] Request exitosa", response);
      return response;
    },
    enabled: enabled && Boolean(videoId),
    refetchInterval: (data) => {
      if (!data) {
        return intervalMs;
      }

      if (TERMINAL_STATUSES.includes(data.estado)) {
        return false;
      }

      if (
        stopAtProcessed &&
        (data.estado === "PROCESADO" || data.estado === "CORTAR")
      ) {
        return false;
      }

      return intervalMs;
    },
  });

  const isTerminal = useMemo(
    () => (query.data ? TERMINAL_STATUSES.includes(query.data.estado) : false),
    [query.data],
  );

  const error = useMemo(() => {
    if (!query.error) {
      return null;
    }

    const apiMessage = axios.isAxiosError<{ message?: string }>(query.error)
      ? query.error.response?.data?.message
      : undefined;
    return typeof apiMessage === "string" && apiMessage.trim().length > 0
      ? apiMessage
      : "No se pudo obtener el estado del video.";
  }, [query.error]);

  return {
    data: query.data ?? null,
    status: query.data?.estado ?? null,
    isPolling: query.isFetching,
    isTerminal,
    error,
    refresh: query.refetch,
  };
}
