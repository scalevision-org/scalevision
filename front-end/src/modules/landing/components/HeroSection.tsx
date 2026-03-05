import { Button } from "@/shared/ui/button";
import { useNavigate } from "react-router-dom";
import React from "react";

export function HeroSection() {
  const navigate = useNavigate();

  return (
    <section className="w-full flex flex-col items-center justify-center text-center py-24 px-6">
      {/* Headline */}
      <h1 className="text-4xl md:text-6xl font-bold tracking-tight max-w-3xl">
        Transforma videos horizontales en
        <span className="text-primary block">
          contenido vertical en segundos
        </span>
      </h1>

      {/* Subheadline */}
      <p className="mt-6 text-lg text-muted-foreground max-w-xl">
        Reencuadre inteligente con IA. Sube tu video y genera una vista previa
        vertical optimizada para redes sociales.
      </p>

      {/* CTA */}
      <div className="mt-10">
        <Button size="lg" onClick={() => navigate("/upload")}>
          Subir video
        </Button>
      </div>
    </section>
  );
}
