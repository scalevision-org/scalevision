import axios from "axios";
import { env } from "@/config/env";

export const httpClient = axios.create({
  baseURL: env.API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});