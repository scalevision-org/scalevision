import React from "react";

export function VideoReframeGraphic() {
  return (
    <section
      id="features"
      className="w-full  border border-border rounded-xl max-w-[960px] mt-16 px-6 py-20"
    >
      <div className="flex flex-col items-center gap-16">
        

      {/* Graphic */}
        <div className="flex items-center justify-center gap-16 flex-wrap">

          {/* Horizontal */}
          <div className="relative w-80 h-44 rounded-xl border border-slate-200 dark:border-[#1F2A27] bg-surface-light dark:bg-surface-dark overflow-hidden shadow-lg">

            {/* Moving subject */}
            <div className="absolute top-1/4 w-16 h-16 bg-primary/20 border border-primary rounded-lg animate-subject" />

            <div className="absolute bottom-2 right-3 text-xs text-text-muted-light dark:text-text-muted-dark">
              16:9
            </div>
          </div>

          {/* Arrow */}
          <div className="hidden md:flex items-center justify-center">
            <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center animate-pulse">
              <span className="text-primary text-2xl">→</span>
            </div>
          </div>

          {/* Vertical */}
          <div className="relative w-44 h-80 rounded-2xl border-2 border-primary bg-surface-light dark:bg-surface-dark shadow-2xl overflow-hidden animate-glow">

            {/* Centered result */}
            <div className="absolute inset-0 flex items-center justify-center">
              <div className="w-20 h-28 bg-primary/20 border border-primary rounded-xl animate-focus" />
            </div>

            <div className="absolute top-2 left-1/2 -translate-x-1/2 text-xs text-text-muted-light dark:text-text-muted-dark">
              9:16
            </div>
          </div>

        </div>

      </div>
    </section>
  )
}
