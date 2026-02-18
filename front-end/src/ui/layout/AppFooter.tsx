import { Logo } from "@/ui/components/Logo"

export function AppFooter() {
  return (
    <footer className="w-full border-t border-slate-200 dark:border-[#1F2A27] bg-surface-light dark:bg-surface-dark mt-24">
      <div className="max-w-7xl mx-auto px-6 lg:px-16 py-12 flex flex-col md:flex-row justify-between items-center gap-6 text-sm text-text-muted-light dark:text-text-muted-dark">
        
        <div className="flex items-center gap-2 opacity-70">
          <Logo />
          <span>© 2026 ScaleVision</span>
        </div>

        <div className="flex gap-8">
          <a href="#privacy" className="hover:text-primary transition-colors">
            Privacy
          </a>
          <a href="#terms" className="hover:text-primary transition-colors">
            Terms
          </a>
          <a href="#contact" className="hover:text-primary transition-colors">
            Contact
          </a>
        </div>
      </div>
    </footer>
  )
}
