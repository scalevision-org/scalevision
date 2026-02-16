export const Config = () => {
  return (
    <div className="flex justify-center px-4 py-10">
      <div className="w-full max-w-2xl">

        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-slate-900 dark:text-white">
            Video Configuration
          </h1>
          <p className="mt-2 text-slate-600 dark:text-slate-400">
            Adjust settings to convert your horizontal video into a vertical short.
          </p>
        </div>

        {/* Card Container */}
        <div className="bg-white dark:bg-slate-900/60 border border-slate-200 dark:border-slate-700 rounded-2xl shadow-sm overflow-hidden">

          {/* Reframing */}
          <div className="p-6">
            <h2 className="text-xl font-bold text-slate-900 dark:text-white">
              Reframing Method
            </h2>
            <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
              Choose how the AI should position the frame.
            </p>

            <div className="mt-6 grid grid-cols-1 sm:grid-cols-2 gap-5">

              {/* Center Crop */}
              <div className="group cursor-pointer rounded-xl border-2 border-transparent hover:border-indigo-400 transition-all p-4 bg-slate-50 dark:bg-slate-800/50">

                <div className="aspect-video rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-200 dark:bg-slate-700 relative overflow-hidden">
                  <div className="absolute inset-0 flex items-center justify-center bg-black/10">
                    <div className="w-1/3 h-full border-2 border-indigo-500 bg-indigo-500/10" />
                  </div>
                </div>

                <div className="mt-4 flex items-center justify-between">
                  <p className="font-semibold text-slate-900 dark:text-white">
                    Center Crop
                  </p>
                  <div className="w-4 h-4 rounded-full border-2 border-slate-300 dark:border-slate-600" />
                </div>

                <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
                  Keeps the center fixed
                </p>
              </div>

              {/* Smart Tracking */}
              <div className="group cursor-pointer rounded-xl border-2 border-indigo-500 bg-indigo-50 dark:bg-indigo-500/10 p-4 transition-all">

                <div className="aspect-video rounded-lg border border-indigo-300 dark:border-indigo-500/30 bg-slate-200 dark:bg-slate-700 relative overflow-hidden">
                  <div className="absolute left-1/4 top-0 w-1/3 h-full border-2 border-indigo-500 bg-indigo-500/20">
                    <div className="absolute top-2 left-2 bg-indigo-500 text-white text-[10px] px-2 py-[2px] rounded">
                      TRACKING
                    </div>
                  </div>
                </div>

                <div className="mt-4 flex items-center justify-between">
                  <p className="font-semibold text-slate-900 dark:text-white">
                    Smart Tracking
                  </p>
                  <div className="w-4 h-4 rounded-full bg-indigo-500 flex items-center justify-center">
                    <div className="w-2 h-2 bg-white rounded-full" />
                  </div>
                </div>

                <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
                  AI tracks moving subjects
                </p>
              </div>

            </div>
          </div>

          {/* Fallback */}
          <div className="px-6 pb-6">
            <label className="flex items-start gap-3 p-4 rounded-xl bg-slate-50 dark:bg-slate-800/40 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors cursor-pointer">

              <input
                type="checkbox"
                defaultChecked
                className="mt-1 h-5 w-5 rounded border-2 border-slate-300 dark:border-slate-600 text-indigo-600 focus:ring-indigo-500"
              />

              <div>
                <p className="text-sm font-medium text-slate-900 dark:text-white">
                  Enable fallback logic
                </p>
                <p className="text-xs text-slate-500 dark:text-slate-400">
                  Switch to center crop if no subject is detected
                </p>
              </div>
            </label>
          </div>

          {/* Duration */}
          <div className="px-6 pb-6 border-t border-slate-200 dark:border-slate-700 pt-6">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">
              Short Duration
            </h3>

            <div className="flex flex-wrap gap-3 mt-4">
              {["Auto", "30s", "60s"].map((option, index) => (
                <button
                  key={index}
                  className={`px-5 py-2 rounded-full text-sm font-semibold border-2 transition-all ${
                    index === 0
                      ? "bg-indigo-600 text-white border-indigo-600"
                      : "bg-transparent border-slate-300 dark:border-slate-600 text-slate-600 dark:text-slate-300 hover:border-indigo-500"
                  }`}
                >
                  {option}
                </button>
              ))}
            </div>
          </div>

          {/* CTA */}
          <div className="p-6 bg-slate-50 dark:bg-slate-800/30 border-t border-slate-200 dark:border-slate-700">
            <button className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-4 rounded-xl shadow-lg shadow-indigo-500/20 transition-transform active:scale-[0.98]">
              Generate Preview
            </button>

            <p className="text-center text-xs text-slate-500 dark:text-slate-400 mt-3">
              Estimated processing time: ~45 seconds
            </p>
          </div>

        </div>
      </div>
    </div>
  );
};
