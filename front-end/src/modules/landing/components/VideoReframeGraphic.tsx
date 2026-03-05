import React from "react"
import { Film, Smartphone, ArrowRight, ArrowDown } from "lucide-react"

export function VideoReframeGraphic() {
  return (
    <section
      id="features"
      className="w-full border border-slate-200 dark:border-[#1F2A27] rounded-xl max-w-4xl mt-16 px-6 py-20 bg-surface-light dark:bg-surface-dark"
    >
      <div className="flex flex-col items-center gap-12">
        <h2 className="text-3xl font-black text-text-light dark:text-text-dark text-center">
          Transformacion de video
        </h2>

        {/* Graphic - Responsive Layout */}
        <div className="flex flex-col lg:flex-row items-center justify-center gap-8 lg:gap-16 w-full">
          {/* Horizontal Video */}
          <div className="flex flex-col items-center gap-4">
            <div className="relative w-72 h-40 rounded-xl border-2 border-slate-300 dark:border-slate-700 bg-gradient-to-br from-slate-100 to-slate-200 dark:from-slate-800 dark:to-slate-700 overflow-hidden shadow-lg hover:shadow-xl transition-shadow">
              {/* Icon - Horizontal Video */}
              <div className="absolute top-2 left-2">
                <Film className="w-5 h-5 text-text-muted-light dark:text-text-muted-dark" />
              </div>

              {/* Moving subject */}
              <div className="absolute top-1/3 w-16 h-16 bg-primary/30 border-2 border-primary rounded-lg animate-subject" />

              {/* Aspect ratio indicator */}
              <div className="absolute bottom-3 right-3 text-xs font-semibold text-text-muted-light dark:text-text-muted-dark bg-black/20 px-2 py-1 rounded">
                16:9
              </div>
            </div>
            <span className="text-sm font-semibold text-text-light dark:text-text-dark">
              Video horizontal
            </span>
          </div>

          {/* Arrow - Responsive */}
          <div className="flex lg:flex-col items-center justify-center">
            {/* Desktop Arrow (Horizontal) */}
            <div className="hidden lg:flex items-center justify-center">
              <div className="w-16 h-16 rounded-full bg-primary/10 dark:bg-primary/20 flex items-center justify-center animate-pulse hover:bg-primary/20 transition-colors">
                <ArrowRight className="w-8 h-8 text-primary" />
              </div>
            </div>

            {/* Mobile Arrow (Vertical) */}
            <div className="lg:hidden flex items-center justify-center">
              <div className="w-16 h-16 rounded-full bg-primary/10 dark:bg-primary/20 flex items-center justify-center animate-pulse hover:bg-primary/20 transition-colors">
                <ArrowDown className="w-8 h-8 text-primary" />
              </div>
            </div>
          </div>

          {/* Vertical Video */}
          <div className="flex flex-col items-center gap-4">
            <div className="relative w-40 h-72 rounded-2xl border-3 border-primary bg-gradient-to-br from-primary/20 to-primary/10 dark:from-primary/30 dark:to-primary/20 shadow-2xl overflow-hidden animate-glow hover:shadow-primary/40 transition-shadow">
              {/* Icon - Vertical Video */}
              <div className="absolute top-2 right-2 z-10">
                <Smartphone className="w-5 h-5 text-primary" />
              </div>

              {/* Centered result */}
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="w-24 h-32 bg-primary/40 border-2 border-primary rounded-lg animate-focus" />
              </div>

              {/* Aspect ratio indicator */}
              <div className="absolute bottom-3 left-1/2 -translate-x-1/2 text-xs font-semibold text-primary bg-black/20 px-2 py-1 rounded">
                9:16
              </div>
            </div>
            <span className="text-sm font-semibold text-text-light dark:text-text-dark">
              Video vertical
            </span>
          </div>
        </div>

        {/* Description */}
        <div className="text-center max-w-lg">
          <p className="text-text-muted-light dark:text-text-muted-dark text-sm">
            Transforma tu contenido panoramico en videos verticales
            perfectamente encuadrados y optimizados para redes sociales.
          </p>
        </div>
      </div>
    </section>
  );
}
