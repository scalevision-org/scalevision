import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { Toaster } from "sonner";
import './index.css'
import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
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
)
