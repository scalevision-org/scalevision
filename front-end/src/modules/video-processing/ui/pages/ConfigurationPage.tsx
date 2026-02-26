import React, { useState } from "react"
import { MethodSelector } from "@/modules/configuration/components/MethodSelector"
import { DurationSelector } from "@/modules/configuration/components/DurationSelector"
import { FallbackCheckbox } from "@/modules/configuration/components/FallbackCheckbox"
import { GenerateButton } from "@/modules/configuration/components/GenerateButton"

type ReframingMethod = "center" | "smart"
type Duration = "Auto" | "30s" | "60s"

interface VideoConfiguration {
  method: ReframingMethod
  duration: Duration
  fallbackEnabled: boolean
}

export const ConfigurationPage = () => {
  const [method, setMethod] = useState<ReframingMethod>("smart")
  const [duration, setDuration] = useState<Duration>("Auto")
  const [fallbackEnabled, setFallbackEnabled] = useState(true)

  const handleGenerate = () => {
    const configuration: VideoConfiguration = {
      method,
      duration,
      fallbackEnabled,
    }

    console.log("📹 Video Configuration:", configuration)
    console.log("🎯 Reframing Method:", method)
    console.log("⏱️ Duration:", duration)
    console.log("🔄 Fallback Enabled:", fallbackEnabled)

  }

  return (
    <div className="flex justify-center py-12 px-4">
      <div className="w-full max-w-xl">
        <h2 className="text-3xl font-black mb-2 text-text-light dark:text-text-dark">
          Video Configuration
        </h2>

        <p className="text-text-muted-light dark:text-text-muted-dark mb-6">
          Turn widescreen videos into social-ready vertical formats.
        </p>

        <div className="bg-surface-light dark:bg-surface-dark 
                        border border-slate-200 dark:border-[#1F2A27] 
                        rounded-xl p-6 space-y-6">

          <MethodSelector value={method} onChange={setMethod} />
          <FallbackCheckbox value={fallbackEnabled} onChange={setFallbackEnabled} />
          <DurationSelector value={duration} onChange={setDuration} />
          <GenerateButton onGenerate={handleGenerate} />

        </div>
      </div>
    </div>
  )
}
