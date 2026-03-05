import React from "react";
import { Outlet, useLocation } from "react-router-dom";
import { AppHeader } from "@/app/layout/AppHeader";
import { AppFooter } from "@/app/layout/AppFooter";

export function AppLayout() {
  const location = useLocation();

  return (
    <div className="min-h-screen w-full flex flex-col ">
      <AppHeader />

      <main className="flex-1 w-full">
        <div key={location.pathname} className="page-transition">
          <Outlet />
        </div>
      </main>

      <AppFooter />
    </div>
  );
}
