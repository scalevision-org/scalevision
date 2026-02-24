type RawEnv = ImportMetaEnv;

export interface AppEnv {
  API_BASE_URL: string;
  UPLOAD_MAX_SIZE_MB: number;
  ALLOWED_VIDEO_FORMATS: string[];
}

const requireEnv = (value: string | undefined, key: string): string => {
  if (!value || value.trim().length === 0) {
    throw new Error(`Missing required environment variable: ${key}`);
  }

  return value.trim();
};

const parseNumber = (value: string | undefined, key: string): number => {
  const normalized = requireEnv(value, key);
  const parsed = Number(normalized);

  if (!Number.isFinite(parsed) || parsed <= 0) {
    throw new Error(`Invalid numeric environment variable: ${key}="${normalized}"`);
  }

  return parsed;
};

const parseList = (value: string | undefined, key: string): string[] => {
  const normalized = requireEnv(value, key);
  const items = normalized
    .split(',')
    .map((item) => item.trim().toLowerCase())
    .filter(Boolean);

  if (items.length === 0) {
    throw new Error(`Invalid list environment variable: ${key} must contain at least one value`);
  }

  return items;
};

const rawEnv: RawEnv = import.meta.env;

export const env: AppEnv = Object.freeze({
  API_BASE_URL: requireEnv(rawEnv.VITE_API_BASE_URL, 'VITE_API_BASE_URL'),
  UPLOAD_MAX_SIZE_MB: parseNumber(
    rawEnv.VITE_UPLOAD_MAX_SIZE_MB,
    'VITE_UPLOAD_MAX_SIZE_MB'
  ),
  ALLOWED_VIDEO_FORMATS: parseList(
    rawEnv.VITE_ALLOWED_VIDEO_FORMATS,
    'VITE_ALLOWED_VIDEO_FORMATS'
  ),
});
