import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import UploadDropzone from "../components/UploadDropzone";
import ProgressBar from "../components/ProgressBar";
import { Logo } from "@/ui/components/Logo";
import { Button } from '@/components/ui/button';
import type { VideoFileMetadata } from '../components/UploadDropzone';
import { uploadVideo } from '../infrastructure/uploadVideo.service';

export default function UploadPage() {
  const navigate = useNavigate();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const [pendingFile, setPendingFile] = useState<File | null>(null);
  const [pendingMetadata, setPendingMetadata] = useState<VideoFileMetadata | null>(null);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [validationError, setValidationError] = useState<string | null>(null);
  const [, setActiveJobId] = useState<string | null>(null);

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

    setSelectedFile(pendingFile);
    setIsUploading(true);
    setIsConfirmOpen(false);

    try {
      const uploadResponse = await uploadVideo({
        file: pendingFile,
        duration: pendingMetadata.durationInSeconds,
      });

      const jobId = String(uploadResponse.id);
      setActiveJobId(jobId);

      navigate('/configuration', {
        state: {
          jobId,
          file: pendingFile,
          metadata: pendingMetadata,
          upload: uploadResponse,
        },
      });
    } catch {
      setIsUploading(false);
      setValidationError('No se pudo subir el video al backend. Intenta nuevamente.');
    }
  };

  const handleTryAnother = () => {
    setPendingFile(null);
    setPendingMetadata(null);
    setSelectedFile(null);
    setIsUploading(false);
    setIsConfirmOpen(false);
    setActiveJobId(null);
  };

  const formatSizeMB = (sizeInBytes: number): string => {
    return `${(sizeInBytes / 1024 / 1024).toFixed(1)}MB`;
  };

  const formatDuration = (durationInSeconds: number): string => {
    const minutes = Math.floor(durationInSeconds / 60);
    const seconds = Math.round(durationInSeconds % 60)
      .toString()
      .padStart(2, '0');

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
          <div className="w-full max-w-xl rounded-2xl border border-gray-200 dark:border-white/10 bg-white dark:bg-surface-dark p-6 shadow-xl">
            <h2 className="text-xl sm:text-2xl font-bold text-text-light dark:text-text-dark">
              Tu video cumple los requisitos
            </h2>
            <p className="mt-2 text-sm text-text-muted-light dark:text-text-muted-dark">
              El archivo cumple con el formato, tamaño y duración. ¿Deseas continuar con este video?
            </p>

            <div className="mt-4 rounded-xl border border-gray-200 dark:border-white/10 bg-muted/40 dark:bg-surface-dark/40 p-4">
              <div className="flex flex-col gap-1 text-sm text-text-light dark:text-text-dark">
                <span className="font-semibold">{pendingMetadata.name}</span>
                <span>Formato: {pendingMetadata.format.toUpperCase()}</span>
                <span>Tamaño: {formatSizeMB(pendingMetadata.sizeInBytes)}</span>
                <span>Duración: {formatDuration(pendingMetadata.durationInSeconds)}</span>
              </div>
            </div>

            <div className="mt-6 flex flex-col sm:flex-row gap-3">
              <Button
                type="button"
                onClick={handleContinue}
                className="rounded-full"
              >
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
          </div>
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
        isUploading={isUploading || isConfirmOpen}
      />
      {validationError && (
        <div className="w-full max-w-2xl rounded-xl border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
          {validationError}
        </div>
      )}
      {isUploading && selectedFile && (
        <div className="w-full flex flex-col rounded-sm p-3 border-muted-foreground bg-muted">
          <div className="flex items-center w-full gap-1 ">
            <Logo className="w-6" src="/public/video-player-svgrepo-com.svg" />
            <ProgressBar progress={50} />
          </div>
          <div className="flex justify-between">
            <span className="text-xs font-medium text-muted-foreground">Processing...</span>
            <span className=" text-xs font-medium text-muted-foreground">
              {(selectedFile.size / 1024 / 1024).toFixed(1)} MB
            </span>
          </div>
        </div>
      )}
    </div>
  );
}
