// src/app/routes/AppRouter.tsx
import { BrowserRouter, Routes, Route } from "react-router-dom"
import { AppLayout } from "@/ui/layout/AppLayout";
import LandingPage from "@/modules/landing/pages/LandingPage"
import { PricingPage } from "@/modules/landing/pages/PricingPage"
import { FeaturesPage } from "@/modules/landing/pages/FeaturesPage"
import UploadPage from "@/modules/upload/pages/UploadPage"
import { ConfigurationPage, PreviewPage } from "@/modules/video-processing/ui/pages";
import { DetectedFaceWithVideo } from "@/domain/types/face.types";

export function AppRouter() {
  // Datos de ejemplo: cada cara con su respectivo video
   const detectedFaces: DetectedFaceWithVideo[] = [
     {
       id: '1',
       label: 'Orange Cat',
       imageUrl: 'https://n9.cl/uo31l7',
       videoUrl: 'https://www.youtube.com/shorts/cu0QGh2w72k',
       scale: 2.5,
       origin: 'center',
     },
     {
       id: '2',
       label: 'Black Cat',
       imageUrl: 'https://n9.cl/7dfnf',
       videoUrl: 'https://www.youtube.com/shorts/b8vKTrzKwg4',
       scale: 2.8,
       origin: 'top-left',
     },
     {
       id: '3',
       label: 'White Cat',
       imageUrl: 'https://n9.cl/brvld',
       videoUrl: 'https://www.youtube.com/shorts/pnmkeJc9c1c',
       scale: 2.2,
       origin: 'bottom-right',
     },
   ];

  // Datos de ejemplo: una sola cara con su respectivo video
 // const detectedFacesSingle: DetectedFaceWithVideo[] = [
 //   {
 //     id: '1',
 //     label: 'Orange Cat',
 //     imageUrl: 'https://n9.cl/uo31l7',
 //     videoUrl: 'https://www.youtube.com/shorts/cu0QGh2w72k',
 //     scale: 2.5,
 //     origin: 'center',
 //   },
 // ];

  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/pricing" element={<PricingPage />} />
          <Route path="/features" element={<FeaturesPage />} />
          <Route path="/upload" element={<UploadPage />} />
          <Route path="/configuration" element={<ConfigurationPage />} />
          <Route path="/preview" element={<PreviewPage 
          detectedFaces={detectedFaces}
          onSelectFace={(faceId) => console.log('Selected face:', faceId)}
        />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
