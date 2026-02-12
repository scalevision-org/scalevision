import type { LucideIcon } from "lucide-react"

interface FeatureCardProps {
  icon: LucideIcon
  title: string
  description: string
}

export const FeatureCard = ({
  icon: Icon,
  title,
  description,
}: FeatureCardProps) => {
  return (
    <div className="group flex flex-col gap-4 rounded-xl border border-border bg-card p-6 transition-all hover:border-primary hover:shadow-lg">
      <div className="flex items-center justify-center size-12 rounded-lg bg-primary/10 text-primary">
        <Icon className="size-6" />
      </div>

      <div className="flex flex-col gap-2">
        <h3 className="text-lg font-bold">{title}</h3>
        <p className="text-sm text-muted-foreground leading-relaxed">
          {description}
        </p>
      </div>
    </div>
  )
}
