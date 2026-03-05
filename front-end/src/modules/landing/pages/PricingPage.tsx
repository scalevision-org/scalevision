import React from "react"

export function PricingPage() {
  const plans = [
    {
      name: "Inicial",
      price: "$29",
      period: "/mes",
      description: "Perfecto para comenzar",
      features: [
        "Hasta 10 videos por mes",
        "Resolucion 1080p",
        "Metodos basicos de reencuadre",
        "Soporte por email",
      ],
    },
    {
      name: "Profesional",
      price: "$79",
      period: "/mes",
      description: "Para creadores de contenido",
      popular: true,
      features: [
        "Videos ilimitados",
        "Resolucion 4K",
        "Todos los metodos de reencuadre",
        "Soporte prioritario",
        "Acceso a la API",
        "Panel de analitica",
      ],
    },
    {
      name: "Empresarial",
      price: "Personalizado",
      period: "",
      description: "Para equipos grandes",
      features: [
        "Soporte dedicado",
        "Integraciones a medida",
        "Licencias por volumen",
        "Opcion on-premise",
        "Garantia de SLA",
      ],
    },
  ];

  return (
    <div className="min-h-screen py-24 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-16">
          <h1 className="text-4xl md:text-5xl font-black mb-4 text-text-light dark:text-text-dark">
            Precios simples y transparentes
          </h1>
          <p className="text-xl text-text-muted-light dark:text-text-muted-dark max-w-2xl mx-auto">
            Elige el plan que se adapte a tus necesidades. Escala cuando
            quieras.
          </p>
        </div>

        {/* Pricing Cards */}
        <div className="grid md:grid-cols-3 gap-8">
          {plans.map((plan, index) => (
            <div
              key={index}
              className={`relative rounded-xl border-2 transition-all ${
                plan.popular
                  ? "border-primary bg-primary/5 dark:bg-primary/10 scale-105 shadow-xl shadow-primary/20"
                  : "border-slate-200 dark:border-slate-700 bg-surface-light dark:bg-surface-dark"
              } p-8`}
            >
              {plan.popular && (
                <div className="absolute -top-4 left-1/2 transform -translate-x-1/2">
                  <span className="bg-primary text-white px-4 py-1 rounded-full text-sm font-bold">
                    Mas popular
                  </span>
                </div>
              )}

              <h3 className="text-2xl font-bold text-text-light dark:text-text-dark mb-2">
                {plan.name}
              </h3>
              <p className="text-text-muted-light dark:text-text-muted-dark mb-6">
                {plan.description}
              </p>

              <div className="mb-6">
                <span className="text-4xl font-bold text-text-light dark:text-text-dark">
                  {plan.price}
                </span>
                <span className="text-text-muted-light dark:text-text-muted-dark">
                  {plan.period}
                </span>
              </div>

              <button
                className={`w-full py-3 px-6 rounded-lg font-semibold transition-all mb-8 ${
                  plan.popular
                    ? "bg-primary text-white hover:bg-accent shadow-lg shadow-primary/30"
                    : "border-2 border-primary text-primary hover:bg-primary/10 dark:hover:bg-primary/20"
                }`}
              >
                Comenzar
              </button>

              <div className="space-y-4">
                {plan.features.map((feature, idx) => (
                  <div key={idx} className="flex items-start gap-3">
                    <div className="w-5 h-5 rounded-full bg-primary/20 dark:bg-primary/30 flex items-center justify-center mt-0.5 flex-shrink-0">
                      <div className="w-2 h-2 bg-primary rounded-full" />
                    </div>
                    <span className="text-text-light dark:text-text-dark">
                      {feature}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>

        {/* FAQ Section */}
        <div className="mt-24 max-w-2xl mx-auto">
          <h2 className="text-3xl font-black mb-12 text-center text-text-light dark:text-text-dark">
            Preguntas frecuentes
          </h2>

          <div className="space-y-6">
            {[
              {
                q: "Puedo cancelar cuando quiera?",
                a: "Si, puedes cancelar tu suscripcion cuando quieras sin penalidades.",
              },
              {
                q: "Ofrecen descuentos por pago anual?",
                a: "Si, ahorra 20% al elegir facturacion anual en lugar de mensual.",
              },
              {
                q: "Hay una prueba gratis?",
                a: "Si! Prueba ScaleVision gratis por 14 dias con acceso completo.",
              },
            ].map((item, idx) => (
              <details
                key={idx}
                className="group border-b border-slate-200 dark:border-slate-700 py-6 cursor-pointer"
              >
                <summary className="flex items-center justify-between text-text-light dark:text-text-dark font-semibold">
                  {item.q}
                  <span className="ml-6 flex-shrink-0 transition-transform group-open:rotate-180">
                    ▼
                  </span>
                </summary>
                <p className="mt-4 text-text-muted-light dark:text-text-muted-dark">
                  {item.a}
                </p>
              </details>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
