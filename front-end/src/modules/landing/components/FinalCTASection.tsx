import React from "react"
import { useNavigate } from "react-router-dom"
export function FinalCTASection() {
  const navigate = useNavigate()


  return (
    <section className="w-full bg-bg-light dark:bg-bg-dark/50 mt-24 border-t border-slate-200 dark:border-[#1F2A27]">
      <div className="max-w-4xl mx-auto px-6 py-24 lg:py-32 flex flex-col items-center gap-8 text-center">

        <div className="flex flex-col gap-4">
          <h2 className="text-4xl lg:text-5xl font-black tracking-tight">
            Ready to go viral?
          </h2>

          <p className="text-lg text-text-muted-light dark:text-text-muted-dark max-w-[600px]">
            Join thousands of creators saving hours every week with automated AI reframing.
          </p>
        </div>

        <button
         onClick={() => navigate("/upload")}
          className="
            min-w-[280px]
            h-14
            px-8
            rounded-xl
            bg-primary
            hover:bg-accent
            text-white
            text-lg
            font-bold
            shadow-xl
            shadow-primary/20
            transition
            active:scale-[0.98]
          "
        >
          Get Started Free
        </button>

      </div>
    </section>
  )
}
