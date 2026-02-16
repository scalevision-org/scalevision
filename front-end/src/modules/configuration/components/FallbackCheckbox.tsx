import React, { useState } from "react"

interface FallbackCheckboxProps {
  value?: boolean
  onChange?: (enabled: boolean) => void
}

export const FallbackCheckbox = ({ value, onChange }: FallbackCheckboxProps) => {
  const [isEnabled, setIsEnabled] = useState(value ?? true)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const checked = e.target.checked
    setIsEnabled(checked)
    onChange?.(checked)
  }

  return (
    <div className="border-t border-slate-200 dark:border-slate-700 pt-6">
      <label className="flex items-start gap-3 p-4 rounded-lg bg-bg-light dark:bg-slate-800/40 hover:bg-slate-100 dark:hover:bg-slate-800/60 transition-colors cursor-pointer">
        <input
          type="checkbox"
          checked={isEnabled}
          onChange={handleChange}
          className="mt-1 h-5 w-5 rounded border-2 border-slate-300 dark:border-slate-600
                     text-primary focus:ring-2 focus:ring-primary focus:ring-offset-0 cursor-pointer"
        />

        <div className="flex-1">
          <p className="text-sm font-semibold text-text-light dark:text-text-dark">
            Enable fallback logic
          </p>
          <p className="text-xs text-text-muted-light dark:text-text-muted-dark mt-1">
            Switch to center crop if no subject is detected.
          </p>
        </div>
      </label>
    </div>
  )
}
