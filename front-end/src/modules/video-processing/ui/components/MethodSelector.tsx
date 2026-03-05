import React, { useState } from "react"

type ReframingMethod = "center" | "smart"

interface MethodSelectorProps {
  value?: ReframingMethod
  onChange?: (method: ReframingMethod) => void
}

export const MethodSelector = ({ 
  value, 
  onChange 
}: MethodSelectorProps) => {
  const [selectedMethod, setSelectedMethod] = useState<ReframingMethod>(value || "smart")

  const handleMethodChange = (method: ReframingMethod) => {
    setSelectedMethod(method)
    onChange?.(method)
  }

  return (
    <div className="space-y-4">
      <div>
        <h2 className="text-xl font-bold text-text-light dark:text-text-dark">
          Metodo de reencuadre
        </h2>
        <p className="text-sm text-text-muted-light dark:text-text-muted-dark mt-1">
          Elige como la IA debe posicionar el encuadre.
        </p>

        <div className="mt-6 grid grid-cols-1 sm:grid-cols-2 gap-5">
          {/* Center Crop */}
          <button
            onClick={() => handleMethodChange("center")}
            className={`group cursor-pointer rounded-xl border-2 transition-all p-4 text-left
              ${
                selectedMethod === "center"
                  ? "border-primary bg-primary/10 dark:bg-primary/20"
                  : "border-slate-200 dark:border-slate-700 bg-bg-light dark:bg-surface-dark hover:border-primary/50"
              }`}
          >
            <div className="aspect-video rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 relative overflow-hidden">
              <div className="absolute inset-0 flex items-center justify-center bg-black/5 dark:bg-black/20">
                <div
                  className={`w-1/3 h-full border-2 ${
                    selectedMethod === "center"
                      ? "border-primary"
                      : "border-slate-400 dark:border-slate-600"
                  }`}
                />
              </div>
            </div>

            <div className="mt-4 flex items-center justify-between">
              <p
                className={`font-semibold ${
                  selectedMethod === "center"
                    ? "text-text-light dark:text-text-dark"
                    : "text-text-muted-light dark:text-text-muted-dark"
                }`}
              >
                Corte centrado
              </p>
              <div
                className={`w-5 h-5 rounded-full border-2 flex items-center justify-center transition-all ${
                  selectedMethod === "center"
                    ? "border-primary bg-primary"
                    : "border-slate-300 dark:border-slate-600"
                }`}
              >
                {selectedMethod === "center" && (
                  <div className="w-2 h-2 bg-white rounded-full" />
                )}
              </div>
            </div>

            <p className="text-sm text-text-muted-light dark:text-text-muted-dark mt-1">
              Mantiene el centro fijo
            </p>
          </button>

          {/* Face Tracking */}
          <button
            onClick={() => handleMethodChange("smart")}
            className={`group cursor-pointer rounded-xl border-2 transition-all p-4 text-left
              ${
                selectedMethod === "smart"
                  ? "border-primary bg-primary/10 dark:bg-primary/20"
                  : "border-slate-200 dark:border-slate-700 bg-bg-light dark:bg-surface-dark hover:border-primary/50"
              }`}
          >
            <div className="aspect-video rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 relative overflow-hidden">
              <div
                className={`absolute left-1/4 top-0 w-1/3 h-full border-2 ${
                  selectedMethod === "smart"
                    ? "border-primary bg-primary/20"
                    : "border-slate-400 dark:border-slate-600 bg-slate-200/50 dark:bg-slate-700/50"
                }`}
              >
                <div
                  className={`absolute top-2 left-2 text-white text-[10px] px-2 py-[2px] rounded font-semibold ${
                    selectedMethod === "smart" ? "bg-primary" : "bg-slate-500"
                  }`}
                >
                  SEGUIMIENTO
                </div>
              </div>
            </div>

            <div className="mt-4 flex items-center justify-between">
              <p
                className={`font-semibold ${
                  selectedMethod === "smart"
                    ? "text-text-light dark:text-text-dark"
                    : "text-text-muted-light dark:text-text-muted-dark"
                }`}
              >
                Seguimiento facial
              </p>
              <div
                className={`w-5 h-5 rounded-full border-2 flex items-center justify-center transition-all ${
                  selectedMethod === "smart"
                    ? "border-primary bg-primary"
                    : "border-slate-300 dark:border-slate-600"
                }`}
              >
                {selectedMethod === "smart" && (
                  <div className="w-2 h-2 bg-white rounded-full" />
                )}
              </div>
            </div>

            <p className="text-sm text-text-muted-light dark:text-text-muted-dark mt-1">
              La IA sigue sujetos en movimiento
            </p>
          </button>
        </div>
      </div>
    </div>
  );
}
