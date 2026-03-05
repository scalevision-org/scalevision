import { useEffect, useState } from "react"
import { Sun, Moon } from "lucide-react"
import { Button } from "@/shared/ui/button";

const getInitialTheme = () => {
  const stored = localStorage.getItem("theme");
  if (stored === "dark") {
    return true;
  }
  if (stored === "light") {
    return false;
  }
  return window.matchMedia("(prefers-color-scheme: dark)").matches;
};

export function ThemeToggle() {
  const [isDark, setIsDark] = useState(() => getInitialTheme());

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
  );
}
