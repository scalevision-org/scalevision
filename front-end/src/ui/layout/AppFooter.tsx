import { Logo } from "@/ui/components/Logo"

export function AppFooter() {
  return (
    <footer className="w-full border-t border-border mt-24">
      <div className="w-full px-6 lg:px-16 py-12 flex flex-col md:flex-row justify-between items-center gap-6 text-sm text-muted-foreground">
        
        <div className="flex items-center gap-2 opacity-70">
          <Logo />
          <span>© 2026 ScaleVision</span>
        </div>

        <div className="flex gap-8">
          <a href="#" className="hover:text-primary transition-colors">
            Privacy
          </a>
          <a href="#" className="hover:text-primary transition-colors">
            Terms
          </a>
          <a href="#" className="hover:text-primary transition-colors">
            Contact
          </a>
        </div>
      </div>
    </footer>
  )
}
