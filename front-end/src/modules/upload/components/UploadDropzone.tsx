import { useRef, useState } from 'react';
import { Cloud } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Logo } from '@/ui/components/Logo';

interface UploadDropzoneProps {
  onFileSelect?: (file: File) => void;
  isUploading?: boolean;
}

export default function UploadDropzone({
  onFileSelect,
  isUploading = false,
}: UploadDropzoneProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [isDragActive, setIsDragActive] = useState(false);

  const ACCEPTED_FORMATS = ['video/mp4', 'video/quicktime'];
  const MAX_FILE_SIZE = 500 * 1024 * 1024; // 500MB

  const validateFile = (file: File): boolean => {
    // Validar tipo de archivo
    if (!ACCEPTED_FORMATS.includes(file.type)) {
      console.error('Invalid file type:', file.type);
      return false;
    }

    // Validar tamaño
    if (file.size > MAX_FILE_SIZE) {
      console.error('File too large:', file.size);
      return false;
    }

    return true;
  };

  const handleFileSelect = (file: File) => {
    if (validateFile(file)) {
      onFileSelect?.(file);
    } else {
      console.warn('File validation failed');
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      handleFileSelect(file);
    }
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
      handleFileSelect(file);
    }
  };

  const handleButtonClick = () => {
    inputRef.current?.click();
  };

  return (
    <div
      onDragOver={handleDragOver}
      onDragLeave={handleDragLeave}
      onDrop={handleDrop}
      className={`
        w-full max-w-2xl h-80 flex flex-col justify-center items-center
        border-2 border-dashed rounded-xl transition-all duration-200
        cursor-pointer
        ${
          isDragActive
            ? 'border-primary bg-primary/5'
            : 'border-muted-foreground bg-muted/40 hover:border-primary/50 hover:bg-muted/60'
        }
        ${isUploading ? 'opacity-60 pointer-events-none' : ''}
      `}
    >
      {/* Input hidden */}
      <input
        ref={inputRef}
        type="file"
        accept="video/mp4,video/quicktime"
        hidden
        onChange={handleInputChange}
        disabled={isUploading}
      />

      {/* Contenido */}
      <div className="flex flex-col items-center gap-4 px-4">
        {/* Ícono */}
        <div className="flex justify-center">
          <Cloud
            className={`h-12 w-12 transition-all duration-200 ${
              isDragActive
                ? 'text-primary scale-110'
                : 'text-muted-foreground'
            }`}
          />
        </div>

        {/* Título */}
        <h3 className="text-lg md:text-xl font-semibold text-center max-w-md">
          {isUploading ? 'Uploading...' : 'Drag and drop your video'}
        </h3>

        {/* Subtítulo */}
        <span className="text-sm text-muted-foreground text-center max-w-md">
          or click to browse
        </span>

        {/* Formatos soportados */}
        <p className="text-xs text-muted-foreground text-center">
          Maximum file size: <span className="font-semibold">500MB</span>
          <br />
          Supported: <span className="font-semibold">MP4, MOV</span>
        </p>

        {/* Botón */}
        <Button
          onClick={handleButtonClick}
          disabled={isUploading}
          size="lg"
          className="mt-2 rounded-full px-6 py-2"
        >
          {isUploading ? 'Uploading...' : 'Select file'}
        </Button>
      </div>
    </div>
  );
}
