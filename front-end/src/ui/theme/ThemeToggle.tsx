import { useEffect, useState } from "react"
import { Sun, Moon } from "lucide-react"
import { Button } from "@/shared/ui/button";

export function ThemeToggle() {
  const [isDark, setIsDark] = useState(false)

  useEffect(() => {
    const root = document.documentElement
    const stored = localStorage.getItem("theme")

    if (stored === "dark") {
      root.classList.add("dark")
      setIsDark(true)
    }
  }, [])

  const toggleTheme = () => {
    const root = document.documentElement

    if (root.classList.contains("dark")) {
      root.classList.remove("dark")
      localStorage.setItem("theme", "light")
      setIsDark(false)
    } else {
      root.classList.add("dark")
      localStorage.setItem("theme", "dark")
      setIsDark(true)
    }
  }

  return (
    <Button
      variant="outline"
      size="icon"
      onClick={toggleTheme}
      className="transition-all"
    >
      {isDark ? <Sun size={18} /> : <Moon size={18} />}
    </Button>
  )
}
