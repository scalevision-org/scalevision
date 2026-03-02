import { create } from "zustand";
import type { VideoCutMode } from "@/modules/video-processing/domain/video.types";
import { setMockConfig } from "@/modules/video-processing/api/videoProcessing.api";

type MockScenario = "CENTER_CROP" | "FACE_TRACKING" | "ERROR";
type MockErrorStep = "UPLOAD" | "PROCESANDO" | "PROCESADO" | "CORTANDO" | "FINAL";

setMockConfig("FACE_TRACKING");

interface VideoProcessState {
  videoId?: number;
  mode: VideoCutMode;
  selectedThumbnailId?: string;
  uploadProgress: number;
  mockScenario: MockScenario;
  mockErrorStep?: MockErrorStep;
  setVideoId: (videoId?: number) => void;
  setMode: (mode: VideoCutMode) => void;
  setSelectedThumbnailId: (thumbnailId?: string) => void;
  setUploadProgress: (progress: number) => void;
  setMockScenario: (scenario: MockScenario, errorStep?: MockErrorStep) => void;
  reset: () => void;
}

export const useVideoProcessStore = create<VideoProcessState>((set) => ({
  videoId: undefined,
  mode: "FACE_TRACKING",
  selectedThumbnailId: undefined,
  uploadProgress: 0,
  mockScenario: "FACE_TRACKING",
  mockErrorStep: undefined,
  setVideoId: (videoId) => set({ videoId }),
  setMode: (mode) => set({ mode }),
  setSelectedThumbnailId: (selectedThumbnailId) => set({ selectedThumbnailId }),
  setUploadProgress: (uploadProgress) => set({ uploadProgress }),
  setMockScenario: (scenario, errorStep) => {
    set({ mockScenario: scenario, mockErrorStep: errorStep });
    setMockConfig(scenario, errorStep);
  },
  reset: () =>
    set({
      videoId: undefined,
      selectedThumbnailId: undefined,
      uploadProgress: 0,
      mockScenario: "FACE_TRACKING",
      mockErrorStep: undefined,
      mode: "FACE_TRACKING",
    }),
}));
