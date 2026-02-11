import { AppHeader } from "@/ui/layout/AppHeader"
import { Outlet } from "react-router-dom"
import { AppFooter } from "@/ui/layout/AppFooter"


export function AppLayout () {
  return (
    <div className="min-h-screen flex flex-col bg-background text-foreground transition-colors duration-300">
      
      <AppHeader />

      <main className="flex-1 w-full">
        <Outlet />
      </main>

      <AppFooter />
    </div>
  )
}
