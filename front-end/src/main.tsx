import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { Toaster } from "sonner";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import './index.css'
import App from './App.tsx'

const queryClient = new QueryClient();

createRoot(document.getElementById('root')!).render(
    <QueryClientProvider client={queryClient}>
  <StrictMode>
    <App />
    <Toaster
      toastOptions={{
    className:
      "bg-white dark:bg-surface-dark text-text-light dark:text-text-dark border border-gray-200 dark:border-white/10",
  }}
         position="top-right"
        richColors
        closeButton
    />
  </StrictMode>,

    </QueryClientProvider>

)
