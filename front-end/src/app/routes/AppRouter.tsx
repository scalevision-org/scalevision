// src/app/routes/AppRouter.tsx
import { BrowserRouter, Routes, Route } from "react-router-dom"
import { AppLayout } from "@/app/layout/AppLayout";
import LandingPage from "@/modules/landing/pages/LandingPage"
import { PricingPage } from "@/modules/landing/pages/PricingPage"
import { FeaturesPage } from "@/modules/landing/pages/FeaturesPage"
import UploadPage from "@/modules/video-processing/ui/pages/UploadPage"
import {
  ConfigurationPage,
  PreviewPage,
} from "@/modules/video-processing/ui/pages";

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/pricing" element={<PricingPage />} />
          <Route path="/features" element={<FeaturesPage />} />
          <Route path="/upload" element={<UploadPage />} />
          <Route path="/configuration" element={<ConfigurationPage />} />
          <Route path="/preview" element={<PreviewPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
