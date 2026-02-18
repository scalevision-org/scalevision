
interface Face {
  id: string;
  label: string;
  imageUrl: string;
  scale: number;
  origin?: string;
}

interface SelectFaceToTrackProps {
  faces: Face[];
  selectedFaceId?: string;
  onSelectFace: (faceId: string) => void;
  isVisible?: boolean;
}

export const SelectFaceToTrack = ({
  faces,
  selectedFaceId,
  onSelectFace,
  isVisible = true,
}: SelectFaceToTrackProps) => {
  if (!isVisible || !faces || faces.length <= 1) {
    return null;
  }

  return (
    <div className="flex  flex-col items-center gap-3 w-full md:w-auto">
      <span className="text-[10px] font-bold uppercase tracking-[0.2em] text-gray-400 dark:text-gray-500">
        Select Face to Track
      </span>
      <div className="flex flex-wrap md:flex-nowrap items-center justify-center gap-3 bg-white/5 dark:bg-white/5 p-3 md:p-2 rounded-full border border-gray-200 dark:border-white/10">
        {faces.map((face, index) => {
          const isSelected = selectedFaceId === face.id;
          const isFirst = index === 0;

          return (
            <button
              key={face.id}
              onClick={() => onSelectFace(face.id)}
              className={`group  relative flex flex-col items-center transition-all ${
                !isFirst && !isSelected ? 'opacity-60 hover:opacity-100' : ''
              }`}
              aria-label={`Select ${face.label}`}
            >
              <div
                className={`relative size-16 rounded-full overflow-hidden transition-all ${
                  isSelected
                    ? 'border-2 border-primary ring-4 ring-primary/20'
                    : 'border-2 border-transparent group-hover:border-gray-400 dark:group-hover:border-gray-500'
                }`}
              >
                <img
                  alt={face.label}
                  className="w-full h-full object-cover"
                  style={{
                    transform: `scale(${face.scale})`,
                    transformOrigin: face.origin || 'center',
                  }}
                  src={face.imageUrl}
                />
              </div>
              <div
                className={`absolute -bottom-1 text-white text-[8px] px-1.5 py-0.5 rounded-full font-bold transition-all ${
                  isSelected
                    ? 'bg-primary block'
                    : 'bg-gray-500 hidden group-hover:block'
                }`}
              >
                {face.label}
              </div>
            </button>
          );
        })}
      </div>
    </div>
  );
};
