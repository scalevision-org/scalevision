import { AppHeader } from "@/ui/layout/AppHeader"
import { Outlet } from "react-router-dom"
import { AppFooter } from "@/ui/layout/AppFooter"
import React from "react"


export function AppLayout() {
  return (
    <div className="min-h-screen w-full flex flex-col ">

      <AppHeader />

      <main className="flex-1 w-full">
        <Outlet />
      </main>

      <AppFooter />
    </div>
  )
}
