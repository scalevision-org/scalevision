import React from "react"
import { Link } from "react-router-dom"

export function FeaturesPage() {
  const features = [
    {
      icon: "🎯",
      title: "Reencuadre inteligente",
      description:
        "Seguimiento del sujeto con IA que encuadra tu contenido automaticamente para cualquier plataforma.",
    },
    {
      icon: "⚡",
      title: "Ultra rapido",
      description:
        "Procesa videos en segundos con infraestructura en la nube y aceleracion por GPU.",
    },
    {
      icon: "🎬",
      title: "Multiples metodos",
      description:
        "Elige entre corte centrado, seguimiento inteligente y metodos personalizados para control total.",
    },
    {
      icon: "📱",
      title: "Multiplataforma",
      description:
        "Ajuste automatico para Instagram, TikTok, YouTube Shorts, LinkedIn y mas con un clic.",
    },
    {
      icon: "🔄",
      title: "Procesamiento por lotes",
      description:
        "Procesa varios videos a la vez y ahorra horas de edicion manual.",
    },
    {
      icon: "📊",
      title: "Analitica",
      description:
        "Mide el rendimiento de tus videos y descubre que funciona mejor.",
    },
    {
      icon: "🔗",
      title: "Integracion API",
      description:
        "Integracion fluida con tu flujo de trabajo mediante nuestra API REST.",
    },
    {
      icon: "🛡️",
      title: "Seguro y privado",
      description:
        "Seguridad de nivel empresarial con borrado automatico y cifrado de extremo a extremo.",
    },
  ];

  return (
    <div className="min-h-screen py-24 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-16">
          <h1 className="text-4xl md:text-5xl font-black mb-4 text-text-light dark:text-text-dark">
            Funciones potentes para creadores de contenido
          </h1>
          <p className="text-xl text-text-muted-light dark:text-text-muted-dark max-w-2xl mx-auto">
            Todo lo que necesitas para convertir videos horizontales en
            contenido vertical viral.
          </p>
        </div>

        {/* Features Grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6 mb-24">
          {features.map((feature, index) => (
            <div
              key={index}
              className="group p-6 rounded-xl border border-slate-200 dark:border-slate-700 
                         bg-surface-light dark:bg-surface-dark hover:border-primary/50 
                         hover:shadow-lg hover:shadow-primary/10 transition-all"
            >
              <div className="text-4xl mb-4">{feature.icon}</div>
              <h3 className="text-lg font-bold text-text-light dark:text-text-dark mb-2">
                {feature.title}
              </h3>
              <p className="text-sm text-text-muted-light dark:text-text-muted-dark">
                {feature.description}
              </p>
            </div>
          ))}
        </div>

        {/* Comparison Section */}
        <div className="mb-24">
          <h2 className="text-3xl font-black mb-12 text-center text-text-light dark:text-text-dark">
            Por que elegir ScaleVision?
          </h2>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b-2 border-slate-200 dark:border-slate-700">
                  <th className="text-left py-4 px-6 font-bold text-text-light dark:text-text-dark">
                    Caracteristica
                  </th>
                  <th className="text-center py-4 px-6 font-bold text-text-light dark:text-text-dark">
                    ScaleVision
                  </th>
                  <th className="text-center py-4 px-6 font-bold text-text-muted-light dark:text-text-muted-dark">
                    Otros
                  </th>
                </tr>
              </thead>
              <tbody>
                {[
                  "Reencuadre inteligente con IA",
                  "Procesamiento por lotes",
                  "Multiples formatos de salida",
                  "Vista previa en tiempo real",
                  "Acceso a la API",
                  "Soporte dedicado",
                ].map((feature, idx) => (
                  <tr
                    key={idx}
                    className="border-b border-slate-200 dark:border-slate-700 hover:bg-primary/5 dark:hover:bg-primary/10 transition-colors"
                  >
                    <td className="py-4 px-6 text-text-light dark:text-text-dark">
                      {feature}
                    </td>
                    <td className="py-4 px-6 text-center text-green-600 dark:text-green-400 text-xl">
                      ✓
                    </td>
                    <td className="py-4 px-6 text-center text-slate-400 dark:text-slate-500 text-xl">
                      ✗
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* CTA Section */}
        <div className="bg-primary/10 dark:bg-primary/20 rounded-xl p-12 text-center">
          <h2 className="text-3xl font-black mb-4 text-text-light dark:text-text-dark">
            Listo para transformar tus videos?
          </h2>
          <p className="text-text-muted-light dark:text-text-muted-dark mb-8 max-w-2xl mx-auto">
            Unete a miles de creadores que ya usan ScaleVision para crear videos
            virales.
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Link
              to="/upload"
              className="bg-primary hover:bg-accent text-white font-bold py-3 px-8 rounded-lg transition-all shadow-lg shadow-primary/30 inline-block"
            >
              Comenzar prueba gratis
            </Link>
            <Link
              to="/pricing"
              className="border-2 border-primary text-primary hover:bg-primary/10 dark:hover:bg-primary/20 font-bold py-3 px-8 rounded-lg transition-all inline-block"
            >
              Ver precios
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
