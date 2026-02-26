import React, { useState } from "react"

type Duration = "Auto" | "30s" | "60s"

interface DurationSelectorProps {
  value?: Duration
  onChange?: (duration: Duration) => void
}

export const DurationSelector = ({ value, onChange }: DurationSelectorProps) => {
  const options: Duration[] = ["Auto", "30s", "60s"]
  const [selectedDuration, setSelectedDuration] = useState<Duration>(value || "Auto")

  const handleDurationChange = (duration: Duration) => {
    setSelectedDuration(duration)
    onChange?.(duration)
  }

  return (
    <div className="border-t border-slate-200 dark:border-slate-700 pt-6">
      <h4 className="text-lg font-bold text-text-light dark:text-text-dark">Short Duration</h4>
      <p className="text-sm text-text-muted-light dark:text-text-muted-dark mt-1">
        Select the maximum duration for generated clips
      </p>
      <div className="flex flex-wrap gap-3 mt-4">
        {options.map((option) => (
          <button
            key={option}
            onClick={() => handleDurationChange(option)}
            className={`px-5 py-2 rounded-full text-sm font-semibold border-2 transition-all
              ${
                selectedDuration === option
                  ? "bg-primary text-white border-primary shadow-md shadow-primary/30"
                  : "border-slate-200 dark:border-slate-700 text-text-light dark:text-text-dark hover:border-primary hover:text-primary"
              }`}
          >
            {option}
          </button>
        ))}
      </div>
    </div>
  )
}
