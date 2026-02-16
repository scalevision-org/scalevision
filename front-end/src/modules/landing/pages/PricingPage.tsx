import React from "react"

export function PricingPage() {
  const plans = [
    {
      name: "Starter",
      price: "$29",
      period: "/month",
      description: "Perfect for getting started",
      features: [
        "Up to 10 videos per month",
        "1080p resolution",
        "Basic reframing methods",
        "Email support",
      ],
    },
    {
      name: "Professional",
      price: "$79",
      period: "/month",
      description: "For content creators",
      popular: true,
      features: [
        "Unlimited videos",
        "4K resolution",
        "All reframing methods",
        "Priority support",
        "API access",
        "Analytics dashboard",
      ],
    },
    {
      name: "Enterprise",
      price: "Custom",
      period: "",
      description: "For large teams",
      features: [
        "Dedicated support",
        "Custom integrations",
        "Volume licensing",
        "On-premise option",
        "SLA guarantee",
      ],
    },
  ]

  return (
    <div className="min-h-screen py-24 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-16">
          <h1 className="text-4xl md:text-5xl font-black mb-4 text-text-light dark:text-text-dark">
            Simple, Transparent Pricing
          </h1>
          <p className="text-xl text-text-muted-light dark:text-text-muted-dark max-w-2xl mx-auto">
            Choose the plan that fits your needs. Scale up or down anytime.
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
                    Most Popular
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
                Get Started
              </button>

              <div className="space-y-4">
                {plan.features.map((feature, idx) => (
                  <div key={idx} className="flex items-start gap-3">
                    <div className="w-5 h-5 rounded-full bg-primary/20 dark:bg-primary/30 flex items-center justify-center mt-0.5 flex-shrink-0">
                      <div className="w-2 h-2 bg-primary rounded-full" />
                    </div>
                    <span className="text-text-light dark:text-text-dark">{feature}</span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>

        {/* FAQ Section */}
        <div className="mt-24 max-w-2xl mx-auto">
          <h2 className="text-3xl font-black mb-12 text-center text-text-light dark:text-text-dark">
            Frequently Asked Questions
          </h2>

          <div className="space-y-6">
            {[
              {
                q: "Can I cancel anytime?",
                a: "Yes, you can cancel your subscription anytime without any penalties.",
              },
              {
                q: "Do you offer discounts for annual billing?",
                a: "Yes, save 20% when you choose annual billing instead of monthly.",
              },
              {
                q: "Is there a free trial?",
                a: "Absolutely! Try ScaleVision free for 14 days with full access to all features.",
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
  )
}
