import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import axios from "axios";
import {
  getVideoStatus,
  type VideoStatusResponse,
} from "@/modules/video-processing/api/videoProcessing.api";
import type { VideoStatus } from "@/modules/video-processing/domain/video.types";

interface UseVideoStatusOptions {
  videoId?: number | string;
  enabled?: boolean;
  intervalMs?: number;
}

const TERMINAL_STATUSES: VideoStatus[] = ["CORTADO", "ERROR"];

export function useVideoStatus({
  videoId,
  enabled = true,
  intervalMs = 3000,
}: UseVideoStatusOptions) {
  const [data, setData] = useState<VideoStatusResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isPolling, setIsPolling] = useState(false);
  const timerRef = useRef<number | null>(null);
  const inFlightRef = useRef(false);

  const isTerminal = useMemo(
    () => (data ? TERMINAL_STATUSES.includes(data.estado) : false),
    [data],
  );

  const clearTimer = () => {
    if (timerRef.current !== null) {
      window.clearTimeout(timerRef.current);
      timerRef.current = null;
    }
  };

  const fetchStatus = useCallback(async () => {
    if (!videoId) {
      return null;
    }

    if (inFlightRef.current) {
      return data;
    }

    inFlightRef.current = true;
    setIsPolling(true);
    setError(null);

    console.info("[Status] Enviando request", {
      endpoint: `/videos/estado/${videoId}`,
      videoId,
    });

    try {
      const response = await getVideoStatus(videoId);
      setData(response);
      console.info("[Status] Request exitosa", response);
      return response;
    } catch (err) {
      const apiMessage = axios.isAxiosError<{ message?: string }>(err)
        ? err.response?.data?.message
        : undefined;
      const message =
        typeof apiMessage === "string" && apiMessage.trim().length > 0
          ? apiMessage
          : "No se pudo obtener el estado del video.";
      console.error("[Status] Request fallida", err);
      setError(message);
      return null;
    } finally {
      inFlightRef.current = false;
      setIsPolling(false);
    }
  }, [data, videoId]);

  const refresh = useCallback(async () => {
    return fetchStatus();
  }, [fetchStatus]);

  useEffect(() => {
    if (!enabled || !videoId) {
      clearTimer();
      return;
    }

    let cancelled = false;

    const tick = async () => {
      const response = await fetchStatus();
      if (cancelled) {
        return;
      }

      const status = response?.estado;
      if (status && TERMINAL_STATUSES.includes(status)) {
        return;
      }

      timerRef.current = window.setTimeout(tick, intervalMs);
    };

    void tick();

    return () => {
      cancelled = true;
      clearTimer();
    };
  }, [enabled, fetchStatus, intervalMs, videoId]);

  return {
    data,
    status: data?.estado ?? null,
    isPolling,
    isTerminal,
    error,
    refresh,
  };
}
