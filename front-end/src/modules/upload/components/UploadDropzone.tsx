import { useRef, useState } from 'react';
import DropzoneArea from './DropzoneArea';
import DropzoneContent from './DropzoneContent';

export interface VideoFileMetadata {
  name: string;
  format: string;
  sizeInBytes: number;
  durationInSeconds: number;
}

interface UploadDropzoneProps {
  onFileValidated?: (file: File, metadata: VideoFileMetadata) => void;
  onValidationError?: (message: string) => void;
  isUploading?: boolean;
}

export default function UploadDropzone({
  onFileValidated,
  onValidationError,
  isUploading = false,
}: UploadDropzoneProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [isDragActive, setIsDragActive] = useState(false);

  const MAX_FILE_SIZE = 150 * 1024 * 1024; // 150MB
  const MAX_DURATION_SECONDS = 120; // 2 minutes

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

  const getVideoDuration = (file: File): Promise<number> =>
    new Promise((resolve, reject) => {
      const video = document.createElement('video');
      const objectUrl = URL.createObjectURL(file);

      video.preload = 'metadata';
      video.onloadedmetadata = () => {
        const duration = video.duration;
        URL.revokeObjectURL(objectUrl);
        resolve(duration);
      };
      video.onerror = () => {
        URL.revokeObjectURL(objectUrl);
        reject(new Error('No se pudo leer la duración del video.'));
      };

      video.src = objectUrl;
    });

  const validateFile = async (file: File): Promise<VideoFileMetadata | null> => {
    const extension = file.name.split('.').pop()?.toLowerCase() ?? '';
    const fileFormat = extension || file.type || 'desconocido';
    const isMp4ByExtension = extension === 'mp4';
    const isMp4ByMime = file.type === 'video/mp4';

    let durationInSeconds = 0;
    try {
      durationInSeconds = await getVideoDuration(file);
    } catch {
      onValidationError?.(
        'No pudimos leer la duración. Tu video debe ser MP4, durar máximo 2 minutos y pesar máximo 150MB.'
      );
      return null;
    }

    const isInvalidFormat = !isMp4ByExtension && !isMp4ByMime;
    const isInvalidSize = file.size > MAX_FILE_SIZE;
    const isInvalidDuration = durationInSeconds > MAX_DURATION_SECONDS;

    if (isInvalidFormat || isInvalidSize || isInvalidDuration) {
      onValidationError?.(
        `Nombre: ${file.name} · Formato: ${fileFormat} · Tamaño: ${formatSizeMB(file.size)} · Duración: ${formatDuration(durationInSeconds)}. Debe ser MP4, máximo 150MB y máximo 2 minutos.`
      );
      return null;
    }

    return {
      name: file.name,
      format: fileFormat,
      sizeInBytes: file.size,
      durationInSeconds,
    };
  };

  const handleFileSelect = async (file: File) => {
    const metadata = await validateFile(file);
    if (metadata) {
      onFileValidated?.(file, metadata);
    } else {
      console.warn('File validation failed');
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      void handleFileSelect(file);
    }

    e.target.value = '';
  };

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragActive(true);
  };

  const handleDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragActive(false);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragActive(false);

    const file = e.dataTransfer.files?.[0];
    if (file) {
      void handleFileSelect(file);
    }
  };

  const handleButtonClick = () => {
    inputRef.current?.click();
  };

  return (
    <>
      {/* Input hidden */}
      <input
        ref={inputRef}
        type="file"
        accept="video/mp4,.mp4"
        hidden
        onChange={handleInputChange}
        disabled={isUploading}
      />

      {/* Dropzone Area */}
      <DropzoneArea
        isDragActive={isDragActive}
        isUploading={isUploading}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        <DropzoneContent
          isDragActive={isDragActive}
          isUploading={isUploading}
          onSelectClick={handleButtonClick}
        />
      </DropzoneArea>
    </>
  );
}
