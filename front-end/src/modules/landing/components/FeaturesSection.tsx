import { ScanFace, Highlighter, Send } from "lucide-react"
import { FeatureCard } from "./FeatureCard"
import React from "react"

const features = [
  {
    icon: ScanFace,
    title: "AI Face Tracking",
    description:
      "Keep the subject perfectly centered automatically, no matter how much they move.",
  },
  {
    icon: Highlighter,
    title: "4K Export",
    description:
      "Maintain crystal clear high resolution for all social platforms with no loss in quality.",
  },
  {
    icon: Send,
    title: "One-Click Sharing",
    description:
      "Push your reframed clips directly to TikTok, Reels, and YouTube Shorts.",
  },
]

export const FeaturesSection = () => {
  return (
    <section
      id="features"
      className=" w-full max-w-3xl px-6 py-16 lg:py-24"
    >
      <div className="flex flex-col gap-12">
        <div className="flex flex-col gap-4 text-center lg:text-left">
          <h2 className="text-3xl lg:text-4xl font-bold tracking-tight">
            Effortless Conversion
          </h2>

          <p className="text-lg text-muted-foreground max-w-[720px]">
            Powering the next generation of content creators with smart automation that saves hours of manual editing.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {features.map((feature) => (
            <FeatureCard
              key={feature.title}
              icon={feature.icon}
              title={feature.title}
              description={feature.description}
            />
          ))}
        </div>
      </div>
    </section>
  )
}
