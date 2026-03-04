import React, { useEffect, useMemo, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { MethodSelector } from "../components/MethodSelector";
import { GenerateButton } from "../components/GenerateButton";
import { useUploadFlow } from "@/modules/video-processing/application/useUploadFlow";
import type { VideoFileMetadata } from "../components/UploadDropzone";
import { useVideoProcessStore } from "@/modules/video-processing/application/videoProcess.store";
import { Button } from "@/shared/ui/button";

type ReframingMethod = "center" | "smart";

type UploadMode = "FACE_TRACKING" | "CENTER_CROP";

interface ConfigurationState {
  file?: File;
  metadata?: VideoFileMetadata;
}

export const ConfigurationPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const {
    submitUpload,
    isUploading,
    error: uploadError,
    clearError,
  } = useUploadFlow();
  const { mode, setMode, setVideoId, uploadProgress, setUploadProgress } =
    useVideoProcessStore();
  const progressTimerRef = useRef<number | null>(null);

  const state = location.state as ConfigurationState | null;
  const file = state?.file;

  const method = useMemo<ReframingMethod>(
    () => (mode === "FACE_TRACKING" ? "smart" : "center"),
    [mode],
  );

  const uploadMode = useMemo<UploadMode>(() => mode, [mode]);

  const clearProgressTimer = () => {
    if (progressTimerRef.current !== null) {
      window.clearInterval(progressTimerRef.current);
      progressTimerRef.current = null;
    }
  };

  const startMockProgress = (sizeBytes: number) => {
    clearProgressTimer();
    setUploadProgress(0);

    if (sizeBytes <= 0) {
      return;
    }

    const totalBytes = sizeBytes;
    const stepBytes = Math.max(totalBytes / 20, 512 * 1024);
    let uploadedBytes = 0;

    progressTimerRef.current = window.setInterval(() => {
      uploadedBytes += stepBytes;
      const rawPercent = Math.round((uploadedBytes / totalBytes) * 100);
      const cappedPercent = Math.min(95, Math.max(0, rawPercent));
      setUploadProgress(cappedPercent);

      if (cappedPercent >= 95) {
        clearProgressTimer();
      }
    }, 200);
  };

  const handleMethodChange = (value: ReframingMethod) => {
    setMode(value === "smart" ? "FACE_TRACKING" : "CENTER_CROP");
  };

  const handleGenerate = async () => {
    if (!file) {
      return;
    }

    clearError();
    startMockProgress(file.size);

    const result = await submitUpload({
      file,
      mode: uploadMode,
    });

    if (!result) {
      clearProgressTimer();
      setUploadProgress(0);
      return;
    }

    setVideoId(Number(result.jobId));
    setUploadProgress(100);
    clearProgressTimer();

    navigate("/preview", {
      state: {
        jobId: result.jobId,
        upload: result.uploadResponse,
      },
    });
  };

  useEffect(() => {
    return () => {
      if (progressTimerRef.current !== null) {
        window.clearInterval(progressTimerRef.current);
      }
    };
  }, []);

  return (
    <div className="flex justify-center py-12 px-4">
      <div className="w-full max-w-xl">
        <h2 className="text-3xl font-black mb-2 text-text-light dark:text-text-dark">
          Video Configuration
        </h2>

        <p className="text-text-muted-light dark:text-text-muted-dark mb-6">
          Turn widescreen videos into social-ready vertical formats.
        </p>

        <div className="bg-surface-light dark:bg-surface-dark border border-slate-200 dark:border-[#1F2A27] rounded-xl p-6 space-y-6">
          {isUploading ? (
            <div className="flex flex-col items-center justify-center gap-6 py-10">
              <div className="h-12 w-12 rounded-full border-4 border-primary/30 border-t-primary animate-spin" />
              <div className="w-full max-w-md space-y-2">
                <div className="text-xs text-text-muted-light dark:text-text-muted-dark text-center">
                  Subiendo: {uploadProgress}%
                </div>
                <div className="h-2 w-full rounded-full bg-slate-200/70 dark:bg-white/10 overflow-hidden">
                  <div
                    className="h-full rounded-full bg-primary transition-all duration-300"
                    style={{ width: `${uploadProgress}%` }}
                  />
                </div>
              </div>
            </div>
          ) : (
            <>
              <MethodSelector value={method} onChange={handleMethodChange} />
              <p className="text-xs text-text-muted-light dark:text-text-muted-dark">
                Si no detecta caras u objetos, el modelo usara un corte
                centrado.
              </p>
              {uploadError && (
                <div className="w-full rounded-xl border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                  {uploadError}
                </div>
              )}
              {!file && (
                <div className="w-full rounded-xl border border-amber-300/40 bg-amber-100/60 px-4 py-3 text-sm text-amber-800">
                  Selecciona un video en la pagina de upload para continuar.
                </div>
              )}
              <GenerateButton
                onGenerate={handleGenerate}
                disabled={isUploading || !file}
              />
            </>
          )}
        </div>
      </div>
    </div>
  );
};
