import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import UploadDropzone from "../components/UploadDropzone";
import { Button } from "@/shared/ui/button";
import type { VideoFileMetadata } from "../components/UploadDropzone";

export default function UploadPage() {
  const navigate = useNavigate();
  const [pendingFile, setPendingFile] = useState<File | null>(null);
  const [pendingMetadata, setPendingMetadata] =
    useState<VideoFileMetadata | null>(null);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [validationError, setValidationError] = useState<string | null>(null);

  const handleFileValidated = (file: File, metadata: VideoFileMetadata) => {
    setPendingFile(file);
    setPendingMetadata(metadata);
    setValidationError(null);
    setIsConfirmOpen(true);
  };

  const handleValidationError = (message: string) => {
    setValidationError(message);
  };

  const handleContinue = async () => {
    if (!pendingFile || !pendingMetadata) {
      return;
    }

    setIsConfirmOpen(false);

    navigate("/configuration", {
      state: {
        file: pendingFile,
        metadata: pendingMetadata,
      },
    });
  };

  const handleConfirmSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    void handleContinue();
  };

  const handleTryAnother = () => {
    setPendingFile(null);
    setPendingMetadata(null);
    setIsConfirmOpen(false);
  };

  const formatSizeMB = (sizeInBytes: number): string => {
    return `${(sizeInBytes / 1024 / 1024).toFixed(1)}MB`;
  };

  const formatDuration = (durationInSeconds: number): string => {
    const minutes = Math.floor(durationInSeconds / 60);
    const seconds = Math.round(durationInSeconds % 60)
      .toString()
      .padStart(2, "0");

    return `${minutes}:${seconds}`;
  };

  return (
    <div className="min-h-screen  w-full flex flex-col gap-10 items-center justify-start py-24 px-6">
      {isConfirmOpen && pendingMetadata && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4"
          role="dialog"
          aria-modal="true"
        >
          <form
            onSubmit={handleConfirmSubmit}
            className="w-full max-w-xl rounded-2xl border border-gray-200 dark:border-white/10 bg-white dark:bg-surface-dark p-6 shadow-xl"
          >
            <h2 className="text-xl sm:text-2xl font-bold text-text-light dark:text-text-dark">
              Tu video cumple los requisitos
            </h2>
            <p className="mt-2 text-sm text-text-muted-light dark:text-text-muted-dark">
              El archivo cumple con el formato, tamaño y duración. ¿Deseas
              continuar con este video?
            </p>

            <div className="mt-4 rounded-xl border border-gray-200 dark:border-white/10 bg-muted/40 dark:bg-surface-dark/40 p-4">
              <div className="flex flex-col gap-1 text-sm text-text-light dark:text-text-dark">
                <span className="font-semibold">{pendingMetadata.name}</span>
                <span>Formato: {pendingMetadata.format.toUpperCase()}</span>
                <span>Tamaño: {formatSizeMB(pendingMetadata.sizeInBytes)}</span>
                <span>
                  Duración:{" "}
                  {Number.isFinite(pendingMetadata.durationInSeconds)
                    ? formatDuration(pendingMetadata.durationInSeconds)
                    : "no disponible"}
                </span>
              </div>
            </div>

            <p className="mt-3 text-xs text-text-muted-light dark:text-text-muted-dark">
              Formatos permitidos: MP4 (H.264/H.265), AVI, MOV, MKV, WEBM, MPEG,
              MPG. Máximo 100MB y 50 segundos.
            </p>

            <div className="mt-6 flex flex-col sm:flex-row gap-3">
              <Button type="submit" className="rounded-full">
                Continuar
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={handleTryAnother}
                className="rounded-full"
              >
                Probar otro video
              </Button>
            </div>
          </form>
        </div>
      )}
      <div className="flex flex-col gap-5">
        <h1 className="text-4xl md:text-6xl font-bold tracking-tight max-w-3xl">
          Convert to Vertical
        </h1>
        <span className="text-lg text-muted-foreground max-w-xl">
          Transform your horizontal clips into viral shorts in seconds
        </span>
      </div>
      <UploadDropzone
        onFileValidated={handleFileValidated}
        onValidationError={handleValidationError}
        isUploading={isConfirmOpen}
      />
      {validationError && (
        <div className="w-full max-w-2xl rounded-xl border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
          {validationError}
        </div>
      )}
    </div>
  );
}
