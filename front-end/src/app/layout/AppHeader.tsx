import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Logo } from "@/shared/ui/Logo";
import { ThemeToggle } from "@/ui/theme/ThemeToggle";
import { Button } from "@/shared/ui/button";

export function AppHeader() {
  const [isOpen, setIsOpen] = useState(false);

  const menuItems = [
    { label: "Precios", href: "/pricing" },
    { label: "Caracteristicas", href: "/features" },
  ];

  return (
    <header className="w-full border-b border-slate-200 dark:border-[#1F2A27] bg-surface-light dark:bg-surface-dark transition-colors">
      <div className="max-w-7xl mx-auto px-6 lg:px-16 py-4 flex items-center justify-between">
        <Link
          to="/"
          className="flex items-center gap-2 z-50"
          onClick={() => setIsOpen(false)}
        >
          <Logo />
          <span className="font-bold text-lg tracking-tight text-text-light dark:text-text-dark">
            ScaleVision
          </span>
        </Link>

        <nav className="hidden md:flex items-center gap-8 text-sm text-text-muted-light dark:text-text-muted-dark">
          {menuItems.map((item) => (
            <a
              key={item.href}
              href={item.href}
              className="hover:text-primary transition-colors"
            >
              {item.label}
            </a>
          ))}
        </nav>

        <div className="hidden md:flex items-center gap-4">
          <ThemeToggle />
          <Button size="sm">Iniciar sesion</Button>
        </div>

        <div className="md:hidden flex items-center gap-3">
          <ThemeToggle />
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="p-2 rounded-lg hover:bg-primary/10 dark:hover:bg-primary/20 transition-colors"
            aria-label="Alternar menu"
          >
            <svg
              className="w-6 h-6 text-text-light dark:text-text-dark transition-transform"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              {isOpen ? (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              ) : (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 6h16M4 12h16M4 18h16"
                />
              )}
            </svg>
          </button>
        </div>
      </div>

      {isOpen && (
        <div className="md:hidden border-t border-slate-200 dark:border-[#1F2A27] bg-surface-light dark:bg-surface-dark">
          <div className="px-6 py-4 space-y-4">
            {menuItems.map((item) => (
              <a
                key={item.href}
                href={item.href}
                className="block py-2 px-4 rounded-lg text-text-light dark:text-text-dark hover:bg-primary/10 dark:hover:bg-primary/20 transition-colors"
                onClick={() => setIsOpen(false)}
              >
                {item.label}
              </a>
            ))}
            <div className="pt-4 border-t border-slate-200 dark:border-[#1F2A27]">
              <Button size="sm" className="w-full">
                Iniciar sesion
              </Button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}
