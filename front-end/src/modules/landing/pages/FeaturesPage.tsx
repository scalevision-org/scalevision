import React from "react"
import { Link } from "react-router-dom"

export function FeaturesPage() {
  const features = [
    {
      icon: "🎯",
      title: "Smart Reframing",
      description: "AI-powered subject tracking that automatically frames your content perfectly for any platform.",
    },
    {
      icon: "⚡",
      title: "Lightning Fast",
      description: "Process videos in seconds with our optimized cloud infrastructure and GPU acceleration.",
    },
    {
      icon: "🎬",
      title: "Multiple Methods",
      description: "Choose between center crop, smart tracking, and custom reframing methods for complete control.",
    },
    {
      icon: "📱",
      title: "Multi-Platform",
      description: "Auto-resize for Instagram, TikTok, YouTube Shorts, LinkedIn, and more in one click.",
    },
    {
      icon: "🔄",
      title: "Batch Processing",
      description: "Process multiple videos simultaneously and save hours of manual editing work.",
    },
    {
      icon: "📊",
      title: "Analytics",
      description: "Track your video performance and get insights on what works best for your audience.",
    },
    {
      icon: "🔗",
      title: "API Integration",
      description: "Seamless integration with your existing workflow through our comprehensive REST API.",
    },
    {
      icon: "🛡️",
      title: "Secure & Private",
      description: "Enterprise-grade security with automatic deletion and end-to-end encryption.",
    },
  ]

  return (
    <div className="min-h-screen py-24 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-16">
          <h1 className="text-4xl md:text-5xl font-black mb-4 text-text-light dark:text-text-dark">
            Powerful Features for Content Creators
          </h1>
          <p className="text-xl text-text-muted-light dark:text-text-muted-dark max-w-2xl mx-auto">
            Everything you need to turn widescreen videos into viral vertical content.
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
            Why Choose ScaleVision?
          </h2>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b-2 border-slate-200 dark:border-slate-700">
                  <th className="text-left py-4 px-6 font-bold text-text-light dark:text-text-dark">
                    Feature
                  </th>
                  <th className="text-center py-4 px-6 font-bold text-text-light dark:text-text-dark">
                    ScaleVision
                  </th>
                  <th className="text-center py-4 px-6 font-bold text-text-muted-light dark:text-text-muted-dark">
                    Others
                  </th>
                </tr>
              </thead>
              <tbody>
                {[
                  "AI-Powered Smart Reframing",
                  "Batch Processing",
                  "Multiple Output Formats",
                  "Real-time Preview",
                  "API Access",
                  "Dedicated Support",
                ].map((feature, idx) => (
                  <tr
                    key={idx}
                    className="border-b border-slate-200 dark:border-slate-700 hover:bg-primary/5 dark:hover:bg-primary/10 transition-colors"
                  >
                    <td className="py-4 px-6 text-text-light dark:text-text-dark">{feature}</td>
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
            Ready to Transform Your Videos?
          </h2>
          <p className="text-text-muted-light dark:text-text-muted-dark mb-8 max-w-2xl mx-auto">
            Join thousands of content creators who are already using ScaleVision to create viral videos.
          </p>
          <div className="flex gap-4 justify-center flex-wrap">
            <Link 
              to="/upload"
              className="bg-primary hover:bg-accent text-white font-bold py-3 px-8 rounded-lg transition-all shadow-lg shadow-primary/30 inline-block"
            >
              Start Free Trial
            </Link>
            <Link 
              to="/pricing"
              className="border-2 border-primary text-primary hover:bg-primary/10 dark:hover:bg-primary/20 font-bold py-3 px-8 rounded-lg transition-all inline-block"
            >
              View Pricing
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
