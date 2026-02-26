import { Cloud } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface DropzoneContentProps {
  isDragActive: boolean;
  isUploading: boolean;
  onSelectClick: () => void;
}

export default function DropzoneContent({
  isDragActive,
  isUploading,
  onSelectClick,
}: DropzoneContentProps) {
  return (
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
        Maximum file size: <span className="font-semibold">150MB</span>
        <br />
        Maximum duration: <span className="font-semibold">2 minutes</span>
        <br />
        Supported: <span className="font-semibold">MP4</span>
        <br />
        Supported: <span className="font-semibold">MP4</span>
      </p>

      {/* Botón */}
      <Button
        onClick={onSelectClick}
        disabled={isUploading}
        size="lg"
        className="mt-2 rounded-full px-6 py-2"
      >
        {isUploading ? 'Uploading...' : 'Select file'}
      </Button>
    </div>
  );
}
