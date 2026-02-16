// src/app/routes/AppRouter.tsx
import { BrowserRouter, Routes, Route } from "react-router-dom"
import { AppLayout } from "@/ui/layout/AppLayout";
import LandingPage from "@/modules/landing/pages/LandingPage"
import UploadPage from "@/modules/upload/pages/UploadPage"
import { ConfigurationPage } from "@/modules/configuration/pages/ConfigurationPage";
import { Config } from "@/modules/configuration/pages/Config";
export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/upload" element={<UploadPage />} />
          <Route path="/configuration" element={<ConfigurationPage />} />
          <Route path="/config" element={<Config />} />
          
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
