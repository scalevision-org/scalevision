import { ReactNode } from 'react';

interface DropzoneAreaProps {
  isDragActive: boolean;
  isUploading: boolean;
  onDragOver: (e: React.DragEvent<HTMLDivElement>) => void;
  onDragLeave: (e: React.DragEvent<HTMLDivElement>) => void;
  onDrop: (e: React.DragEvent<HTMLDivElement>) => void;
  children: ReactNode;
}

export default function DropzoneArea({
  isDragActive,
  isUploading,
  onDragOver,
  onDragLeave,
  onDrop,
  children,
}: DropzoneAreaProps) {
  return (
    <div
      onDragOver={onDragOver}
      onDragLeave={onDragLeave}
      onDrop={onDrop}
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
      {children}
    </div>
  );
}
