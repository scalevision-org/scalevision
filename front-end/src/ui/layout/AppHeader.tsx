import { Link } from "react-router-dom"
import { cn } from "@/lib/utils"
import { Logo } from "@/ui/components/Logo"
import { ThemeToggle } from "@/ui/theme/ThemeToggle"
import { Button } from "@/components/ui/button"

export function AppHeader() {
  return (
    <header className="w-full border-b border-slate-200 dark:border-[#1F2A27] bg-surface-light dark:bg-surface-dark transition-colors">
      <div className="w-full px-6 lg:px-16 py-4 flex items-center justify-between">
        
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2">
          <Logo />
          <span className="font-bold text-lg tracking-tight">
            ScaleVision
          </span>
        </Link>

        {/* Nav */}
        <nav className="hidden md:flex items-center gap-8 text-sm text-muted-foreground">
          <a href="#" className="hover:text-primary transition-colors">
            Pricing
          </a>
          <a href="#features" className="hover:text-primary transition-colors">
            Features
          </a>
        </nav>

        {/* Actions */}
        <div className="flex items-center gap-4">
          <ThemeToggle />
          <Button size="sm">Sign In</Button>
        </div>
      </div>
    </header>
  )
}
