
import './App.css'
import { AppLayout } from "@/ui/layout/AppLayout";
import LandingPage from "@/modules/landing/pages/LandingPage"
import { AppRouter } from './app/routes/AppRouter';

function App() {
  return (
    <>
      <AppRouter />
    </>
  )
}

export default App