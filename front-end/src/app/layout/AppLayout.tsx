import React from "react";
import { Outlet } from "react-router-dom";
import { AppHeader } from "@/app/layout/AppHeader";
import { AppFooter } from "@/app/layout/AppFooter";

export function AppLayout() {
  return (
    <div className="min-h-screen w-full flex flex-col ">
      <AppHeader />

      <main className="flex-1 w-full">
        <Outlet />
      </main>

      <AppFooter />
    </div>
  );
}
