import { useEffect, useRef, useState } from "react";
import { Play, Image as ImageIcon } from "lucide-react";

interface VideoPreviewMockProps {
  videoUrl?: string;
  videoType?: "image" | "video" | "embed" | "youtube";
  isPlaying?: boolean;
  onPlayPauseToggle?: (isPlaying: boolean) => void;
  isLoading?: boolean;
}

const extractYouTubeId = (url: string): string | null => {
  const patterns = [
    /youtube\.com\/shorts\/([a-zA-Z0-9_-]{11})/,
    /youtube\.com\/watch\?v=([a-zA-Z0-9_-]{11})/,
    /youtu\.be\/([a-zA-Z0-9_-]{11})/,
    /youtube\.com\/embed\/([a-zA-Z0-9_-]{11})/,
  ];

  for (const pattern of patterns) {
    const match = url.match(pattern);
    if (match) return match[1];
  }
  return null;
};

const isDirectVideoUrl = (url: string): boolean => {
  const hasVideoExtension = /\.(mp4|webm|ogg|mov)(\?|$)/i.test(url);
  const isCloudinaryVideo = /\/video\/upload\//i.test(url);
  return hasVideoExtension || isCloudinaryVideo;
};

export const VideoPreviewMock = ({
  videoUrl,
  videoType: initialVideoType = "image",
  isPlaying: initialIsPlaying = false,
  onPlayPauseToggle,
  isLoading = false,
}: VideoPreviewMockProps) => {
  const [isPlaying, setIsPlaying] = useState(initialIsPlaying);
  const videoRef = useRef<HTMLVideoElement | null>(null);

  // Detectar si es URL de YouTube automáticamente
  let videoType = initialVideoType;
  let effectiveUrl = videoUrl;

  if (videoUrl && videoType === "image") {
    const videoId = extractYouTubeId(videoUrl);
    if (videoId) {
      videoType = "youtube";
      effectiveUrl = `https://www.youtube.com/embed/${videoId}`;
    } else if (isDirectVideoUrl(videoUrl)) {
      videoType = "video";
    }
  }

  useEffect(() => {
    setIsPlaying(initialIsPlaying);
  }, [initialIsPlaying]);

  const handlePlayPause = async () => {
    if (videoType !== "video") {
      const newState = !isPlaying;
      setIsPlaying(newState);
      onPlayPauseToggle?.(newState);
      return;
    }

    const videoElement = videoRef.current;
    if (!videoElement) {
      return;
    }

    if (videoElement.paused) {
      try {
        await videoElement.play();
        setIsPlaying(true);
        onPlayPauseToggle?.(true);
      } catch {
        setIsPlaying(false);
        onPlayPauseToggle?.(false);
      }
    } else {
      videoElement.pause();
      setIsPlaying(false);
      onPlayPauseToggle?.(false);
    }
  };

  const renderPlaceholder = () => (
    <div className="absolute inset-0 bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex flex-col items-center justify-center">
      <div className="animate-pulse">
        <ImageIcon className="w-12 h-12 text-gray-600 mb-3" />
      </div>
      <p className="text-gray-500 text-xs text-center px-4">
        No hay video disponible
      </p>
    </div>
  );

  const renderVideoContent = () => {
    if (!effectiveUrl || isLoading) {
      return renderPlaceholder();
    }

    switch (videoType) {
      case "video":
        return (
          <video
            ref={videoRef}
            className="absolute inset-0 w-full h-full object-cover"
            src={effectiveUrl}
            autoPlay={isPlaying}
            muted
            playsInline
            preload="metadata"
            controls
            onPlay={() => {
              setIsPlaying(true);
              onPlayPauseToggle?.(true);
            }}
            onPause={() => {
              setIsPlaying(false);
              onPlayPauseToggle?.(false);
            }}
            onEnded={() => {
              setIsPlaying(false);
              onPlayPauseToggle?.(false);
            }}
          />
        );
      case "embed":
      case "youtube":
        return (
          <iframe
            className="absolute inset-0 w-full h-full border-none"
            src={effectiveUrl}
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowFullScreen
            loading="lazy"
          />
        );
      case "image":
      default:
        return (
          <div
            className="absolute inset-0 bg-cover bg-center"
            style={{
              backgroundImage: `url('${effectiveUrl}')`,
            }}
          >
            <div className="absolute inset-0 bg-black/20"></div>
          </div>
        );
    }
  };

  return (
    <div className="relative phone-mockup w-full max-w-[280px] h-[560px] bg-black rounded-[2.5rem] p-3 shadow-2xl ring-8 ring-gray-200 dark:ring-gray-800 border-4 border-gray-100 dark:border-gray-900 overflow-hidden">
      {/* Notch */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-28 h-6 bg-gray-200 dark:bg-gray-900 rounded-b-2xl z-20"></div>

      {/* Screen Content */}
      <div className="relative h-full w-full rounded-[1.8rem] overflow-hidden bg-gray-900">
        {/* Video Content */}
        {renderVideoContent()}

        {/* Video Controls Overlay - Solo mostrar si hay URL */}
        {effectiveUrl && !isLoading && videoType !== "youtube" && (
          <div className="absolute inset-0 flex flex-col justify-between p-6 pointer-events-none">
            {/* Top Menu */}
            <div className="pointer-events-auto flex justify-end">
              <button className="text-white/80 hover:text-white transition-colors">
                <svg
                  className="w-6 h-6"
                  fill="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z" />
                </svg>
              </button>
            </div>

            {/* Center Play Button */}
            <div className="pointer-events-auto flex flex-col items-center justify-center grow">
              <button
                onClick={handlePlayPause}
                className="flex items-center justify-center rounded-full size-16 bg-black/40 text-white backdrop-blur-sm border border-white/20 hover:scale-105 transition-transform hover:bg-primary/60"
                aria-label={isPlaying ? "Pausar video" : "Reproducir video"}
              >
                {isPlaying ? (
                  <svg className="w-8 h-8 fill-current" viewBox="0 0 24 24">
                    <path d="M6 4h4v16H6V4zm8 0h4v16h-4V4z" />
                  </svg>
                ) : (
                  <Play className="w-8 h-8 fill-current" />
                )}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
