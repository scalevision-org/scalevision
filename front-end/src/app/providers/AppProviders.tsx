import type { PropsWithChildren } from "react";
import { Toaster } from "sonner";
import { QueryProvider } from "@/app/providers/QueryProvider";

export function AppProviders({ children }: PropsWithChildren) {
  return (
    <QueryProvider>
      {children}
      <Toaster
        toastOptions={{
          className:
            "bg-white dark:bg-surface-dark text-text-light dark:text-text-dark border border-gray-200 dark:border-white/10",
        }}
        position="top-right"
        richColors
        closeButton
      />
    </QueryProvider>
  );
}
