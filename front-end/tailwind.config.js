import type { Config } from "tailwindcss"

export default {
  darkMode: "class",
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#10B981",
        accent: "#34D399",

        "bg-light": "#F9FAFB",
        "bg-dark": "#0E1513",

        "surface-light": "#FFFFFF",
        "surface-dark": "#16201D",

        "text-light": "#1F2937",
        "text-dark": "#ECFDF5",

        "text-muted-light": "#6B7280",
        "text-muted-dark": "#A7F3D0",
      },
      fontFamily: {
        sans: ["Inter", "sans-serif"],
      },
    },
  },
  plugins: [],
} satisfies Config

