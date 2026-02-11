import type { SVGProps } from "react"

export function Logo({ className, ...rest }: SVGProps<SVGSVGElement>) {
	return (
		<svg
			viewBox="0 0 64 64"
			className={className ?? "h-8 w-8"}
			role="img"
			aria-label="ScaleVision"
			{...rest}
		>
			<defs>
				<linearGradient id="sv-gradient" x1="0" y1="0" x2="1" y2="1">
					<stop offset="0%" stopColor="#111827" />
					<stop offset="100%" stopColor="#0ea5e9" />
				</linearGradient>
			</defs>
			<circle cx="32" cy="32" r="28" fill="url(#sv-gradient)" />
			<circle cx="32" cy="32" r="14" fill="white" opacity="0.9" />
			<circle cx="32" cy="32" r="6" fill="#111827" />
		</svg>
	)
}

