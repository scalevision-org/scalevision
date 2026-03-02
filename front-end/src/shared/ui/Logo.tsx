import type { ImgHTMLAttributes } from "react";

type LogoProps = ImgHTMLAttributes<HTMLImageElement>;

export function Logo({ className, alt = "ScaleVision", ...rest }: LogoProps) {
  return (
    <img
      src="/scalevision_circular.svg"
      className={className ?? "h-8 w-8"}
      alt={alt}
      {...rest}
    />
  );
}
