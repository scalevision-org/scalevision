// src/app/routes/AppRouter.tsx
import { BrowserRouter, Routes, Route } from "react-router-dom"
import UploadPage from "@infrastructure/ui/UploadPage"

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<UploadPage />} />
      </Routes>
    </BrowserRouter>
  )
}
