import React from "react"
import { HeroSection } from "../components/HeroSection"
import { FeaturesSection } from "../components/FeaturesSection"
import { VideoReframeGraphic } from "../components/VideoReframeGraphic"
import { FinalCTASection } from "../components/FinalCTASection"

export default function LandingPage() {
  return (
   <div className="flex flex-col items-center">
      <HeroSection />
      <VideoReframeGraphic />
      <FeaturesSection />
      <FinalCTASection />
    </div>
  )
}
