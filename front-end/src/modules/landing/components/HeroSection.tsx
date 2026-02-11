import { Button } from "@/components/ui/button"
import { useNavigate } from "react-router-dom"

export function HeroSection() {
  const navigate = useNavigate()

  return (
    <section className="w-full flex flex-col items-center justify-center text-center py-24 px-6">
      
      {/* Headline */}
      <h1 className="text-4xl md:text-6xl font-bold tracking-tight max-w-3xl">
        Transform Horizontal Videos into
        <span className="text-primary block">
          Vertical Content in Seconds
        </span>
      </h1>

      {/* Subheadline */}
      <p className="mt-6 text-lg text-muted-foreground max-w-xl">
        Smart reframing powered by AI. Upload your video and generate
        a vertical preview optimized for social platforms.
      </p>

      {/* CTA */}
      <div className="mt-10">
        <Button size="lg" onClick={() => navigate("/upload")}>
          Upload Video
        </Button>
      </div>
    </section>
  )
}
