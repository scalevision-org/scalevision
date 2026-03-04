import { Cloud } from 'lucide-react';
import { Button } from "@/shared/ui/button";

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
            isDragActive ? "text-primary scale-110" : "text-muted-foreground"
          }`}
        />
      </div>

      {/* Título */}
      <h3 className="text-lg md:text-xl font-semibold text-center max-w-md">
        {isUploading ? "Subiendo..." : "Arrastra y suelta tu video"}
      </h3>

      {/* Subtítulo */}
      <span className="text-sm text-muted-foreground text-center max-w-md">
        o haz clic para buscar
      </span>

      {/* Formatos soportados */}
      <p className="text-xs text-muted-foreground text-center">
        Tamano maximo de archivo: <span className="font-semibold">100MB</span>
        <br />
        Duracion maxima: <span className="font-semibold">50 segundos</span>
        <br />
        Compatible:{" "}
        <span className="font-semibold">
          MP4 (H.264/H.265), AVI, MOV, MKV, WEBM, MPEG, MPG
        </span>
      </p>

      {/* Botón */}
      <Button
        onClick={onSelectClick}
        disabled={isUploading}
        size="lg"
        className="mt-2 rounded-full px-6 py-2"
      >
        {isUploading ? "Subiendo..." : "Seleccionar archivo"}
      </Button>
    </div>
  );
}
