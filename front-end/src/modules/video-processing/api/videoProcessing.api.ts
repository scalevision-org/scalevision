import { httpClient } from "@/infrastructure/http/httpClient";
import type {
  CutVideoRequestDto,
  UploadVideoRequestDto,
  UploadVideoResponseDto,
  VideoFinalResponseDto,
  VideoStatusResponseDto,
  VideoThumbnailsResponseDto,
} from "@/modules/video-processing/domain/video.types";

type MockScenario = "CENTER_CROP" | "FACE_TRACKING" | "ERROR";
type MockErrorStep =
  | "UPLOAD"
  | "PROCESANDO"
  | "PROCESADO"
  | "CORTANDO"
  | "FINAL";

interface MockVideoEntry {
  id: number;
  mode: "FACE_TRACKING" | "CENTER_CROP";
  statusIndex: number;
  cutRequested: boolean;
  errorStep?: MockErrorStep;
  thumbnails: VideoThumbnailsResponseDto;
  finalUrl: string;
}

let mockScenario: MockScenario = "CENTER_CROP";
let mockErrorStep: MockErrorStep | undefined;
let mockIdCounter = 1000;
const mockStore = new Map<number, MockVideoEntry>();

export function setMockConfig(
  scenario: MockScenario,
  errorStep?: MockErrorStep,
) {
  mockScenario = scenario;
  mockErrorStep = errorStep;
}

const centerFlow: Array<
  "SUBIDO" | "PROCESANDO" | "PROCESADO" | "CORTANDO" | "CORTADO"
> = ["SUBIDO", "PROCESANDO", "PROCESADO", "CORTANDO", "CORTADO"];

const faceFlow: Array<
  "SUBIDO" | "PROCESANDO" | "PROCESADO" | "CORTAR" | "CORTANDO" | "CORTADO"
> = ["SUBIDO", "PROCESANDO", "PROCESADO", "CORTAR", "CORTANDO", "CORTADO"];

const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

const buildThumbnails = (id: number): VideoThumbnailsResponseDto => ({
  id,
  url_mini_vista_01: "https://n9.cl/f5txu",
  url_mini_vista_02: "https://n9.cl/brvld",
  url_mini_vista_03: "https://n9.cl/b0h5t",
});

const buildFinalUrl = (id: number) =>
  `https://placehold.co/360x640?text=Final+${id}`;

export async function uploadVideo({
  file,
  mode,
}: UploadVideoRequestDto): Promise<UploadVideoResponseDto> {
  // Real call (enable when backend is ready)
  // const formData = new FormData();
  // formData.append("file", file);
  // formData.append("modo_corte", mode);
  // const { data } = await httpClient.post<UploadVideoResponseDto>(
  //   "/videos/subir",
  //   formData,
  // );
  // return data;

  await wait(500);
  if (mockScenario === "ERROR" && mockErrorStep === "UPLOAD") {
    throw new Error("Mock: error al subir el video.");
  }

  const id = mockIdCounter++;
  const entry: MockVideoEntry = {
    id,
    mode,
    statusIndex: 0,
    cutRequested: false,
    thumbnails: buildThumbnails(id),
    finalUrl: buildFinalUrl(id),
  };
  mockStore.set(id, entry);

  return {
    id,
    url_video_original: "https://placehold.co/1280x720?text=Original",
    estado: "SUBIDO",
    modo_corte: mode,
  };
}

export async function getVideoStatus(
  videoId: number | string,
): Promise<VideoStatusResponseDto> {
  // Real call (enable when backend is ready)
  // const { data } = await httpClient.get<VideoStatusResponseDto>(
  //   `/videos/estado/${videoId}`,
  // );
  // return data;

  await wait(400);
  const entry = mockStore.get(Number(videoId));
  if (!entry) {
    throw new Error("Mock: video no encontrado.");
  }

  const flow = entry.mode === "CENTER_CROP" ? centerFlow : faceFlow;
  const nextStatus = flow[Math.min(entry.statusIndex, flow.length - 1)];

  if (entry.mode === "FACE_TRACKING" && !entry.cutRequested) {
    if (nextStatus === "CORTAR" || nextStatus === "CORTANDO") {
      return { id: entry.id, estado: "PROCESADO" };
    }
  }

  if (entry.statusIndex < flow.length - 1) {
    entry.statusIndex += 1;
  }

  return { id: entry.id, estado: nextStatus };
}

export async function getVideoThumbnails(
  videoId: number | string,
): Promise<VideoThumbnailsResponseDto> {
  // Real call (enable when backend is ready)
  // const { data } = await httpClient.get<VideoThumbnailsResponseDto>(
  //   `/videos/mini-vistas/${videoId}`,
  // );
  // return data;

  await wait(300);
  const entry = mockStore.get(Number(videoId));
  if (!entry) {
    throw new Error("Mock: mini-vistas no disponibles.");
  }

  return entry.thumbnails;
}

export async function cutVideo(
  videoId: number | string,
  payload: CutVideoRequestDto,
): Promise<{ ok: boolean }> {
  // Real call (enable when backend is ready)
  // const { data } = await httpClient.post<{ ok: boolean }>(
  //   `/videos/cortar-video/${videoId}`,
  //   payload,
  // );
  // return data;

  await wait(400);
  if (!payload.mini_vista_id) {
    throw new Error("Mock: mini_vista_id requerido.");
  }

  const entry = mockStore.get(Number(videoId));
  if (!entry) {
    throw new Error("Mock: video no encontrado.");
  }

  if (payload.mini_vista_id === "mini_01") {
    entry.finalUrl = "https://www.youtube.com/shorts/b8vKTrzKwg4";
  } else if (payload.mini_vista_id === "mini_02") {
    entry.finalUrl = "https://www.youtube.com/shorts/pnmkeJc9c1c";
  } else if (payload.mini_vista_id === "mini_03") {
    entry.finalUrl = "https://www.youtube.com/shorts/cu0QGh2w72k";
  }

  entry.cutRequested = true;
  if (entry.mode === "CENTER_CROP") {
    entry.statusIndex = 3;
  } else {
    entry.statusIndex = faceFlow.indexOf("CORTAR");
  }
  return { ok: true };
}

export async function getFinalVideo(
  videoId: number | string,
): Promise<VideoFinalResponseDto> {
  // Real call (enable when backend is ready)
  // const { data } = await httpClient.get<VideoFinalResponseDto>(
  //   `/videos/final/${videoId}`,
  // );
  // return data;

  await wait(300);
  const entry = mockStore.get(Number(videoId));
  if (!entry) {
    throw new Error("Mock: video final no disponible.");
  }

  if (entry.mode === "CENTER_CROP") {
    entry.finalUrl = "https://www.youtube.com/shorts/b8vKTrzKwg4";
  }

  return { id: entry.id, url_video_final: entry.finalUrl };
}