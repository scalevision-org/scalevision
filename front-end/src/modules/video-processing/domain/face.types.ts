/**
 * Tipo para representar una cara detectada con su video asociado
 */
export interface DetectedFaceWithVideo {
  id: string;
  label: string;
  imageUrl: string;
  videoUrl: string;
  scale: number;
  origin?: string;
}

/**
 * Tipo para una cara sin video (usado en componentes genéricos)
 */
export interface DetectedFace extends Omit<DetectedFaceWithVideo, 'videoUrl'> {
  videoUrl?: string;
}
