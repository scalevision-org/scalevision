import UploadVideo from "../components/UploadVideo";
import ProgressBar from "../components/ProgressBar";
import { Logo } from "@/ui/components/Logo";

export default function UploadPage() {
  return (
    <div className="min-h-screen  w-full flex flex-col gap-10 items-center justify-start py-24 px-6">
      <div className="flex flex-col gap-5">
        <h1 className="text-4xl md:text-6xl font-bold tracking-tight max-w-3xl">
          Convert to Vertical
        </h1>
        <span className="text-lg text-muted-foreground max-w-xl">
          Transform your horizontal clips into viral shorts in seconds
        </span>
      </div>
      <UploadVideo />
      <div className="w-full flex flex-col rounded-sm p-3 border-muted-foreground bg-muted">
        <div className="flex items-center w-full gap-1 ">
          <Logo className="w-6" src="/public/video-player-svgrepo-com.svg" />
          <ProgressBar progress={50} />
        </div>
        <div className="flex justify-between">
          <span className="text-xs font-medium text-muted-foreground">Processing...</span>
        <span className=" text-xs font-medium text-muted-foreground">34.2 MB / 50.2 MB</span>
        </div>
      </div>
    </div>
  );
}
