import { useEffect, useState } from "react"
import { Sun, Moon } from "lucide-react"
import { Button } from "@/shared/ui/button";

export function ThemeToggle() {
  const [isDark, setIsDark] = useState(
    () => localStorage.getItem("theme") === "dark",
  );

  useEffect(() => {
    const root = document.documentElement;
    root.classList.toggle("dark", isDark);
    localStorage.setItem("theme", isDark ? "dark" : "light");
  }, [isDark]);

  const toggleTheme = () => {
    setIsDark((value) => !value);
  };

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
