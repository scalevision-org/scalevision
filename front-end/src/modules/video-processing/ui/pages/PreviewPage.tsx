import { useEffect, useMemo, useState } from "react";
import { VideoPreviewMock } from "../components/VideoPreviewMock";
import { VideoActions } from "../components/VideoActions";
import { useLocation, useNavigate } from "react-router-dom";
import { useVideoStatus } from "@/modules/video-processing/application/useVideoStatus";
import { useVideoThumbnails } from "@/modules/video-processing/application/useVideoThumbnails";
import { useCutVideo } from "@/modules/video-processing/application/useCutVideo";
import { useFinalVideo } from "@/modules/video-processing/application/useFinalVideo";
import { useVideoProcessStore } from "@/modules/video-processing/application/videoProcess.store";
import { Button } from "@/shared/ui/button";

interface PreviewState {
  jobId?: string;
}

export const PreviewPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const state = location.state as PreviewState | null;
  const storeVideoId = useVideoProcessStore((s) => s.videoId);
  const mode = useVideoProcessStore((s) => s.mode);
  const selectedThumbnailId = useVideoProcessStore(
    (s) => s.selectedThumbnailId,
  );
  const setSelectedThumbnailId = useVideoProcessStore(
    (s) => s.setSelectedThumbnailId,
  );
  const [cutSent, setCutSent] = useState(false);

  const jobId =
    storeVideoId ?? (state?.jobId ? Number(state.jobId) : undefined);
  const {
    status,
    isPolling,
    error: statusError,
    refresh: refreshStatus,
  } = useVideoStatus({
    videoId: jobId,
    enabled: Boolean(jobId),
    stopAtProcessed: mode === "FACE_TRACKING" && !cutSent,
  });

  useEffect(() => {
    if (status === "PROCESADO" && mode === "FACE_TRACKING") {
      setCutSent(false);
    }
  }, [mode, status]);

  const showThumbnails = status === "PROCESADO" && mode === "FACE_TRACKING";
  const thumbnailsQuery = useVideoThumbnails({
    videoId: jobId,
    enabled: Boolean(jobId) && showThumbnails,
  });
  const cutMutation = useCutVideo();
  const finalVideoQuery = useFinalVideo({
    videoId: jobId,
    enabled: Boolean(jobId) && status === "CORTADO",
  });

  useEffect(() => {
    if (status === "PROCESADO" && mode === "CENTER_CROP" && !cutSent && !cutMutation.isPending) {
      setCutSent(true);
      if (jobId) {
        cutMutation.mutateAsync({
          videoId: jobId,
          payload: { mini_vista_id: "auto_center_crop" },
        }).then(() => refreshStatus()).catch(console.error);
      }
    }
  }, [status, mode, cutSent, jobId, cutMutation, refreshStatus]);

  const handleProcessAnother = () => {
    navigate("/upload");
  };
  const [isPlayingVideo, setIsPlayingVideo] = useState(false);

  const thumbnailOptions = useMemo(() => {
    if (!thumbnailsQuery.data) {
      return [];
    }

    return [
      {
        id: "mini_01",
        url: thumbnailsQuery.data.url_mini_vista_01,
      },
      {
        id: "mini_02",
        url: thumbnailsQuery.data.url_mini_vista_02,
      },
      {
        id: "mini_03",
        url: thumbnailsQuery.data.url_mini_vista_03,
      },
    ];
  }, [thumbnailsQuery.data]);

  const currentVideoUrl = finalVideoQuery.data?.url_video_final || "";

  const handleCutVideo = async () => {
    if (!jobId || !selectedThumbnailId) {
      return;
    }

    const selectedThumb = thumbnailOptions.find((t) => t.id === selectedThumbnailId);

    await cutMutation.mutateAsync({
      videoId: jobId,
      payload: {
        mini_vista_id: selectedThumb?.url || selectedThumbnailId,
      },
    });

    setCutSent(true);
    void refreshStatus();
  };

  return (
    <div className="min-h-screen bg-bg-light dark:bg-bg-dark transition-colors">
      {(statusError || thumbnailsQuery.error || cutMutation.error || finalVideoQuery.error || !jobId) && (
        <div className="bg-white dark:bg-bg-dark dark:border-white/5 transition-colors">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
            {statusError && (
              <div className="mt-3 w-full max-w-xl rounded-xl border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                {statusError}
              </div>
            )}
            {thumbnailsQuery.error && (
              <div className="mt-3 w-full max-w-xl rounded-xl border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                Error al obtener mini-vistas.
              </div>
            )}
            {cutMutation.error && (
              <div className="mt-3 w-full max-w-xl rounded-xl border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                Error al enviar mini-vista.
              </div>
            )}
            {finalVideoQuery.error && (
              <div className="mt-3 w-full max-w-xl rounded-xl border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                Error al obtener el video final.
              </div>
            )}
            {!jobId && (
              <div className="mt-3 w-full max-w-xl rounded-xl border border-amber-300/40 bg-amber-100/60 px-4 py-3 text-sm text-amber-800">
                No se encontro el id del video. Vuelve a la pagina de upload.
              </div>
            )}
          </div>
        </div>
      )}

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="flex justify-evenly flex-col sm:flex-row gap-8">
          <div className=" flex flex-col  items-center justify-evenly gap-8">
            {showThumbnails && (
              <div className="w-full max-w-md rounded-xl border border-slate-200 dark:border-white/10 bg-white dark:bg-surface-dark p-4">
                <h3 className="text-sm font-semibold text-text-light dark:text-text-dark">
                  Selecciona una mini-vista
                </h3>
                <div className="mt-3 grid grid-cols-3 gap-3">
                  {thumbnailOptions.map((thumb) => (
                    <button
                      key={thumb.id}
                      type="button"
                      onClick={() => setSelectedThumbnailId(thumb.id)}
                      className={`rounded-xl border-4 overflow-hidden transition-all duration-300 ${selectedThumbnailId === thumb.id
                        ? "border-primary scale-105 ring-4 ring-primary/30 shadow-2xl"
                        : "border-transparent opacity-70 hover:opacity-100"
                        }`}
                    >
                      <img
                        src={thumb.url}
                        alt={`Mini vista ${thumb.id}`}
                        className="w-full h-auto"
                      />
                    </button>
                  ))}
                </div>
                <div className="mt-4 flex items-center justify-between">
                  <span className="text-xs text-text-muted-light dark:text-text-muted-dark">
                    {selectedThumbnailId
                      ? `Seleccion: ${selectedThumbnailId}`
                      : "Selecciona una mini-vista"}
                  </span>
                  <Button
                    type="button"
                    onClick={handleCutVideo}
                    disabled={!selectedThumbnailId || cutMutation.isPending}
                  >
                    Enviar mini-vista
                  </Button>
                </div>
              </div>
            )}

            <VideoPreviewMock
              videoUrl={currentVideoUrl}
              videoType={currentVideoUrl ? "video" : undefined}
              isPlaying={isPlayingVideo}
              onPlayPauseToggle={setIsPlayingVideo}
              isProcessing={!currentVideoUrl && status !== "ERROR" && status !== null}
            />

            <div className="text-center">
              <p className="text-text-light dark:text-text-dark text-sm">
                {status === "CORTADO"
                  ? "Video final listo"
                  : "Vista previa del proceso"}
              </p>
              <p className="text-text-muted-light dark:text-text-muted-dark text-xs mt-1">
                {status === "PROCESANDO" || status === "CORTANDO"
                  ? "Procesando..."
                  : status === "PROCESADO" && mode === "CENTER_CROP"
                    ? "Corte automatico en curso"
                    : status === "PROCESADO"
                      ? "Selecciona una mini-vista para cortar"
                      : ""}
              </p>
            </div>

            <VideoActions
              videoUrl={currentVideoUrl}
              onExportTikTok={() => console.log("Export TikTok")}
              onExportYouTube={() => console.log("Export YouTube")}
              onExportInstagram={() => console.log("Export Instagram")}
              onDownload={(blob: Blob) =>
                console.log("Video downloaded:", blob)
              }
              onProcessAnother={handleProcessAnother}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default PreviewPage;
