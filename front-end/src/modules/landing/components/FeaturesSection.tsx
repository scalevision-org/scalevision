import { ScanFace, Highlighter, Send } from "lucide-react"
import { FeatureCard } from "./FeatureCard"
import React from "react"

const features = [
  {
    icon: ScanFace,
    title: "Seguimiento facial con IA",
    description:
      "Mantiene al sujeto perfectamente centrado de forma automatica, sin importar cuanto se mueva.",
  },
  {
    icon: Highlighter,
    title: "Exportacion en 4K",
    description:
      "Conserva una alta resolucion nitida para todas las plataformas sin perdida de calidad.",
  },
  {
    icon: Send,
    title: "Compartir en un clic",
    description:
      "Publica tus clips reencuadrados directo en TikTok, Reels y YouTube Shorts.",
  },
];

export const FeaturesSection = () => {
  return (
    <section id="features" className=" w-full max-w-3xl px-6 py-16 lg:py-24">
      <div className="flex flex-col gap-12">
        <div className="flex flex-col gap-4 text-center lg:text-left">
          <h2 className="text-3xl lg:text-4xl font-bold tracking-tight">
            Conversion sin esfuerzo
          </h2>

          <p className="text-lg text-muted-foreground max-w-[720px]">
            Potenciamos a la proxima generacion de creadores con automatizacion
            inteligente que ahorra horas de edicion manual.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {features.map((feature) => (
            <FeatureCard
              key={feature.title}
              icon={feature.icon}
              title={feature.title}
              description={feature.description}
            />
          ))}
        </div>
      </div>
    </section>
  );
};
