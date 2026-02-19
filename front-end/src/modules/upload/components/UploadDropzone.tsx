import { useRef, useState } from 'react';
import DropzoneArea from './DropzoneArea';
import DropzoneContent from './DropzoneContent';

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
    <>
      {/* Input hidden */}
      <input
        ref={inputRef}
        type="file"
        accept="video/mp4,video/quicktime"
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
