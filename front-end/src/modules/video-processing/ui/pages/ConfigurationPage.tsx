import React, { useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { MethodSelector } from "../components/MethodSelector";
import { GenerateButton } from "../components/GenerateButton";
import { useUploadFlow } from "@/modules/video-processing/application/useUploadFlow";
import type { VideoFileMetadata } from "../components/UploadDropzone";

type ReframingMethod = "center" | "smart";

type UploadMode = "FACE_TRACKING" | "CENTER_CROP";

interface ConfigurationState {
  file?: File;
  metadata?: VideoFileMetadata;
}

export const ConfigurationPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [method, setMethod] = useState<ReframingMethod>("smart");
  const {
    submitUpload,
    isUploading,
    error: uploadError,
    clearError,
  } = useUploadFlow();

  const state = location.state as ConfigurationState | null;
  const file = state?.file;

  const uploadMode = useMemo<UploadMode>(
    () => (method === "smart" ? "FACE_TRACKING" : "CENTER_CROP"),
    [method],
  );

  const handleGenerate = async () => {
    if (!file) {
      return;
    }

    clearError();

    const result = await submitUpload({
      file,
      mode: uploadMode,
    });

    if (!result) {
      return;
    }

    navigate("/preview", {
      state: {
        jobId: result.jobId,
        upload: result.uploadResponse,
      },
    });
  };

  return (
    <div className="flex justify-center py-12 px-4">
      <div className="w-full max-w-xl">
        <h2 className="text-3xl font-black mb-2 text-text-light dark:text-text-dark">
          Video Configuration
        </h2>

        <p className="text-text-muted-light dark:text-text-muted-dark mb-6">
          Turn widescreen videos into social-ready vertical formats.
        </p>

        <div
          className="bg-surface-light dark:bg-surface-dark 
                        border border-slate-200 dark:border-[#1F2A27] 
                        rounded-xl p-6 space-y-6"
        >
          <MethodSelector value={method} onChange={setMethod} />
          <p className="text-xs text-text-muted-light dark:text-text-muted-dark">
            Si no detecta caras u objetos, el modelo usara un corte centrado.
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
        </div>
      </div>
    </div>
  );
};
