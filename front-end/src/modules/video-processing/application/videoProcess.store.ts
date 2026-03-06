import { create } from "zustand";
import type { VideoCutMode } from "@/modules/video-processing/domain/video.types";
interface VideoProcessState {
  videoId?: number;
  mode: VideoCutMode;
  selectedThumbnailId?: string;
  uploadProgress: number;
  setVideoId: (videoId?: number) => void;
  setMode: (mode: VideoCutMode) => void;
  setSelectedThumbnailId: (thumbnailId?: string) => void;
  setUploadProgress: (progress: number) => void;
  reset: () => void;
}

export const useVideoProcessStore = create<VideoProcessState>((set) => ({
  videoId: undefined,
  mode: "FACE_TRACKING",
  selectedThumbnailId: undefined,
  uploadProgress: 0,
  setVideoId: (videoId) => set({ videoId }),
  setMode: (mode) => set({ mode }),
  setSelectedThumbnailId: (selectedThumbnailId) => set({ selectedThumbnailId }),
  setUploadProgress: (uploadProgress) => set({ uploadProgress }),
  reset: () =>
    set({
      videoId: undefined,
      selectedThumbnailId: undefined,
      uploadProgress: 0,
      mode: "FACE_TRACKING",
    }),
}));
