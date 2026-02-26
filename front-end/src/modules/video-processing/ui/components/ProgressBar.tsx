
export default function ProgressBar({progress}: {progress: number}) {

      return (
    <div className="w-full">
      {/* Contenedor */}
      <div className="w-full bg-gray-200 rounded-2xl h-2 overflow-hidden shadow-inner">
        
        {/* Barra */}
        <div
          className="h-full w-full bg-primary rounded-sm
                     transition-all duration-500 ease-out 
                     flex items-center justify-center 
                     text-white text-sm font-semibold"
          style={{ width: `${progress}%` }}
        >
        </div>
      </div>
    </div>
  );
}