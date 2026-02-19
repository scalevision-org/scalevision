import { useState } from 'react';
import UploadDropzone from "../components/UploadDropzone";
import ProgressBar from "../components/ProgressBar";
import { Logo } from "@/ui/components/Logo";

export default function UploadPage() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isUploading, setIsUploading] = useState(false);

  const handleFileSelect = (file: File) => {
    setSelectedFile(file);
    setIsUploading(true);
    // Simulación de upload - en el futuro se reemplaza con lógica de backend/Zustand
    console.log('File selected:', file.name, file.size);
  };

  return (
    <div className="min-h-screen  w-full flex flex-col gap-10 items-center justify-start py-24 px-6">
      <div className="flex flex-col gap-5">
        <h1 className="text-4xl md:text-6xl font-bold tracking-tight max-w-3xl">
          Convert to Vertical
        </h1>
        <span className="text-lg text-muted-foreground max-w-xl">
          Transform your horizontal clips into viral shorts in seconds
        </span>
      </div>
      <UploadDropzone onFileSelect={handleFileSelect} isUploading={isUploading} />
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
