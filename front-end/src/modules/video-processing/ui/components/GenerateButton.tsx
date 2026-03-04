import React from "react"

interface GenerateButtonProps {
  onGenerate?: () => void
  disabled?: boolean
}

export const GenerateButton = ({ onGenerate, disabled }: GenerateButtonProps) => {
  return (
    <div className="pt-6 border-t border-slate-200 dark:border-slate-700">
      <button
        onClick={onGenerate}
        disabled={disabled}
        className="w-full bg-primary hover:bg-accent text-white
                   font-bold py-4 rounded-lg shadow-lg shadow-primary/20
                   transition active:scale-[0.98]
                   disabled:opacity-50 disabled:cursor-not-allowed disabled:active:scale-100"
      >
        Generar vista previa
      </button>

      <p className="text-center text-xs mt-3 text-text-muted-light dark:text-text-muted-dark">
        Tiempo estimado de procesamiento: ~45 segundos
      </p>
    </div>
  );
}
