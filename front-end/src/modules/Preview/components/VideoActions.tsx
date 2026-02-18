import { useState } from 'react';
import { Download } from 'lucide-react';

interface VideoActionsProps {
  videoUrl?: string;
  onExportTikTok?: () => void;
  onExportYouTube?: () => void;
  onExportInstagram?: () => void;
  onDownload?: (videoBlob: Blob) => void;
}

/**
 * Descarga un video desde una URL de YouTube Shorts
 * Nota: YouTube tiene restricciones CORS para descargas directas
 * Esta es una solución alternativa usando un servicio de conversión
 */
const downloadVideoFromYouTube = async (youtubeUrl: string): Promise<Blob | null> => {
  try {
    // Extraer ID del video
    const videoIdMatch = youtubeUrl.match(/(?:youtube\.com\/shorts\/|youtu\.be\/|youtube\.com\/watch\?v=)([a-zA-Z0-9_-]{11})/);
    if (!videoIdMatch) {
      console.error('Invalid YouTube URL');
      return null;
    }

    const videoId = videoIdMatch[1];

    // Opción 1: Usar un servicio de conversión público (ej: yt-dlp, youtube-dl)
    // Nota: Esto requiere un backend que implemente la descarga
    // Por ahora, mostraremos una alternativa para archivos mp4 directos

    // Opción 2: Si tienes acceso directo a un archivo MP4 (por backend)
    const response = await fetch(`/api/download-video?videoId=${videoId}`);

    if (!response.ok) {
      throw new Error('Failed to download video');
    }

    return await response.blob();
  } catch (error) {
    console.error('Error downloading video:', error);
    return null;
  }
};

/**
 * Descarga un archivo blob como archivo local
 */
const triggerBlobDownload = (blob: Blob, filename: string) => {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
};

export const VideoActions = ({
  videoUrl,
  onExportTikTok,
  onExportYouTube,
  onExportInstagram,
  onDownload,
}: VideoActionsProps) => {
  const [isDownloading, setIsDownloading] = useState(false);

  const handleDownload = async () => {
    if (!videoUrl) {
      console.warn('No video URL provided');
      return;
    }

    setIsDownloading(true);
    try {
      // Intenta descargar desde YouTube
      const videoBlob = await downloadVideoFromYouTube(videoUrl);

      if (videoBlob) {
        // Si se obtiene el blob localmente
        triggerBlobDownload(videoBlob, 'video-reframed.mp4');
        onDownload?.(videoBlob);
      } else {
        // Alternativa: guardar el enlace original o redirigir a servicio
        console.log('Descarga no disponible localmente. Abriendo en servicio externo...');
        window.open(videoUrl, '_blank');
      }
    } catch (error) {
      console.error('Download error:', error);
    } finally {
      setIsDownloading(false);
    }
  };

  const handleExportTikTok = () => {
    if (!videoUrl) return;
    // Copiar URL al portapapeles y abrir TikTok
    navigator.clipboard.writeText(videoUrl);
    window.open('https://www.tiktok.com/upload', '_blank');
    onExportTikTok?.();
  };

  const handleExportYouTube = () => {
    if (!videoUrl) return;
    // Copiar URL al portapapeles y abrir YouTube Studio
    navigator.clipboard.writeText(videoUrl);
    window.open('https://studio.youtube.com/uploads', '_blank');
    onExportYouTube?.();
  };

  const handleExportInstagram = () => {
    if (!videoUrl) return;
    // Copiar URL al portapapeles y abrir Instagram
    navigator.clipboard.writeText(videoUrl);
    window.open('https://www.instagram.com/', '_blank');
    onExportInstagram?.();
  };

  return (
    <div className="w-full max-w-[420px] flex flex-col gap-3">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <button
          type="button"
          onClick={handleExportTikTok}
          disabled={!videoUrl}
          className="group inline-flex items-center justify-center gap-2 rounded-full border border-gray-200 dark:border-white/10 bg-white dark:bg-surface-dark px-4 py-2.5 text-sm font-semibold text-text-light dark:text-text-dark transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed hover:enabled:border-primary/50 hover:enabled:bg-gray-50 dark:hover:enabled:bg-surface-dark/80 hover:enabled:text-primary hover:enabled:shadow-md"
        >
          <svg
            className="w-4 h-4 transition-transform group-hover:scale-110"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path d="M19.59 6.69a4.83 4.83 0 0 1-3.77-4.25V2h-3.68v13.67a2.89 2.89 0 1 1-5.08-2.85 2.9 2.9 0 0 1 2.31 1.24V9.44a6.57 6.57 0 0 0-5.79 8.08 6.59 6.59 0 0 0 6.18 4.63 6.62 6.62 0 0 0 6.78-6.59v-2.07a7.7 7.7 0 0 0 3.77 1.87V9.71a4.5 4.5 0 0 1-.36-.04z" />
          </svg>
          Exportar a TikTok
        </button>

        <button
          type="button"
          onClick={handleExportYouTube}
          disabled={!videoUrl}
          className="group inline-flex items-center justify-center gap-2 rounded-full border border-gray-200 dark:border-white/10 bg-white dark:bg-surface-dark px-4 py-2.5 text-sm font-semibold text-text-light dark:text-text-dark transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed hover:enabled:border-primary/50 hover:enabled:bg-gray-50 dark:hover:enabled:bg-surface-dark/80 hover:enabled:text-primary hover:enabled:shadow-md"
        >
          <svg
            className="w-4 h-4 transition-transform group-hover:scale-110"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z" />
          </svg>
          Exportar a YouTube
        </button>

        <button
          type="button"
          onClick={handleExportInstagram}
          disabled={!videoUrl}
          className="group inline-flex items-center justify-center gap-2 rounded-full border border-gray-200 dark:border-white/10 bg-white dark:bg-surface-dark px-4 py-2.5 text-sm font-semibold text-text-light dark:text-text-dark transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed hover:enabled:border-primary/50 hover:enabled:bg-gray-50 dark:hover:enabled:bg-surface-dark/80 hover:enabled:text-primary hover:enabled:shadow-md"
        >
          <svg
            className="w-4 h-4 transition-transform group-hover:scale-110"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path d="M12 0C8.74 0 8.333.015 7.053.072 5.775.132 4.905.333 4.117.6c-.798.272-1.559.645-2.228 1.315-.67.669-1.043 1.43-1.313 2.227-.266.788-.468 1.658-.527 2.936C.008 8.333 0 8.74 0 12s.015 3.667.072 4.947c.06 1.277.261 2.148.527 2.936.271.798.644 1.559 1.313 2.228.669.67 1.43 1.043 2.227 1.313.788.266 1.658.468 2.936.527C8.333 23.992 8.74 24 12 24s3.667-.015 4.947-.072c1.277-.06 2.148-.261 2.936-.527.798-.271 1.559-.644 2.228-1.313.67-.669 1.043-1.43 1.313-2.227.266-.788.468-1.658.527-2.936.06-1.28.072-1.687.072-4.947s-.015-3.667-.072-4.947c-.06-1.277-.261-2.148-.527-2.936-.271-.798-.644-1.559-1.313-2.228-.669-.67-1.43-1.043-2.227-1.313-.788-.266-1.658-.468-2.936-.527C15.667.008 15.26 0 12 0zm0 2.16c3.203 0 3.585.009 4.849.070 1.171.054 1.845.244 2.276.408.572.221.978.484 1.486.992.508.508.771.914.992 1.486.164.43.354 1.105.408 2.276.061 1.264.07 1.646.07 4.849s-.009 3.585-.07 4.849c-.054 1.171-.244 1.845-.408 2.276-.221.572-.484.978-.992 1.486-.508.508-.914.771-1.486.992-.43.164-1.105.354-2.276.408-1.264.061-1.646.07-4.849.07s-3.585-.009-4.849-.07c-1.171-.054-1.845-.244-2.276-.408-.572-.221-.978-.484-1.486-.992-.508-.508-.771-.914-.992-1.486-.164-.43-.354-1.105-.408-2.276-.061-1.264-.07-1.646-.07-4.849s.009-3.585.07-4.849c.054-1.171.244-1.845.408-2.276.221-.572.484-.978.992-1.486.508-.508.914-.771 1.486-.992.43-.164 1.105-.354 2.276-.408 1.264-.061 1.646-.07 4.849-.07zM12 5.838a6.162 6.162 0 100 12.324 6.162 6.162 0 000-12.324zM12 16a4 4 0 110-8 4 4 0 010 8zm4.965-10.322a1.44 1.44 0 11-2.881 0 1.44 1.44 0 012.881 0z" />
          </svg>
          Exportar a Instagram
        </button>

        <button
          type="button"
          onClick={handleDownload}
          disabled={!videoUrl || isDownloading}
          className="group inline-flex items-center justify-center gap-2 rounded-full border border-primary/40 bg-primary/10 px-4 py-2.5 text-sm font-semibold text-primary transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed hover:enabled:border-primary hover:enabled:bg-primary/20 hover:enabled:shadow-md"
        >
          <Download className="w-4 h-4 transition-transform group-hover:scale-110" />
          {isDownloading ? 'Descargando...' : 'Descargar video'}
        </button>
      </div>
      <p className="text-xs text-gray-500 dark:text-gray-400 text-center">
        Al exportar, la URL se copiará al portapapeles y se abrirá la plataforma
      </p>
    </div>
  );
};
