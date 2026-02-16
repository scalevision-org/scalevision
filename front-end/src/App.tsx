import './App.css'
import { AppLayout } from "@/ui/layout/AppLayout";
import LandingPage from "@/modules/landing/pages/LandingPage"
// Ponemos las llaves porque NO es una exportación default
import { AppRouter } from "@/app/routes/AppRouter"; 

function App() {
  return (
    <>
      <AppRouter />
    </>
  )
}

export default App