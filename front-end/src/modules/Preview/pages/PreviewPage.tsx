import { useState, useMemo } from 'react';
import { SelectFaceToTrack } from '../components/SelectFaceToTrack';
import { VideoPreviewMock } from '../components/VideoPreviewMock';
import { VideoActions } from '../components/VideoActions';
import { DetectedFace } from '@/domain/types/face.types';

interface PreviewPageProps {
  detectedFaces?: DetectedFace[];
  onSelectFace?: (faceId: string) => void;
}

export const PreviewPage = ({
  detectedFaces = [],   
  onSelectFace,
}: PreviewPageProps) => {
  const [selectedFaceId, setSelectedFaceId] = useState<string | undefined>(
    detectedFaces[0]?.id
  );
  const [isPlayingVideo, setIsPlayingVideo] = useState(false);

  const hasMultipleFaces = useMemo(
    () => detectedFaces.length > 1,
    [detectedFaces.length]
  );

  // Get the selected face and its video URL
  const selectedFace = useMemo(
    () => detectedFaces.find((f) => f.id === selectedFaceId),
    [selectedFaceId, detectedFaces]
  );

  const currentVideoUrl = selectedFace?.videoUrl || '';

  const handleSelectFace = (faceId: string) => {
    setSelectedFaceId(faceId);
    onSelectFace?.(faceId);
  };

  return (
    <div className="min-h-screen bg-bg-light dark:bg-bg-dark transition-colors">
      {/* Header Section */}
      <div className="bg-white dark:bg-surface-dark border-b border-gray-200 dark:border-white/10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <h1 className="text-gray-900 dark:text-white text-2xl font-bold tracking-tight">
            Reframed Video Preview
          </h1>
          <p className="text-gray-500 dark:text-gray-400 text-sm mt-1">
            {hasMultipleFaces
              ? 'Review your vertical crop and select the subject to track.'
              : 'Previsualiza tu video antes de exportar o compartir'}
          </p>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Face Selection (Hidden on mobile, shown on md+) */}
          {hasMultipleFaces && (
            <SelectFaceToTrack
              faces={detectedFaces}
              selectedFaceId={selectedFaceId}
              onSelectFace={handleSelectFace}
              isVisible={hasMultipleFaces}
            />
          )}

          {/* Video Preview */}
          <div className="flex-1 flex flex-col items-center justify-center gap-8">
            {/* Phone Mockup */}
            <VideoPreviewMock
              videoUrl={currentVideoUrl}
              isPlaying={isPlayingVideo}
              onPlayPauseToggle={setIsPlayingVideo}
            />

            <VideoActions
              videoUrl={currentVideoUrl}
              onExportTikTok={() => console.log('Export TikTok')}
              onExportYouTube={() => console.log('Export YouTube')}
              onExportInstagram={() => console.log('Export Instagram')}
              onDownload={(blob) => console.log('Video downloaded:', blob)}
            />

            {/* Video Info */}
            <div className="text-center">
              <p className="text-text-light dark:text-text-dark text-sm">
                {selectedFaceId
                  ? `Tracking: ${detectedFaces.find((f) => f.id === selectedFaceId)?.label}`
                  : 'Video Preview'}
              </p>
              <p className="text-text-muted-light dark:text-text-muted-dark text-xs mt-1">
                {detectedFaces.length} {detectedFaces.length === 1 ? 'subject' : 'subjects'}{' '}
                detected in video
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PreviewPage;
