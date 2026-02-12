import { Logo } from "@/ui/components/Logo";
import SelectFileButton from "./SelectFileButton";

export default function UploadVideo() {
  return (
    <div className="w-full max-w-7xl h-80">
      <div className=" w-full h-full flex flex-col justify-center items-center  border-dashed border-2 border-muted-foreground rounded-lg">
        <Logo className="h-12 mb-7" src="/public/cloud-up-arrow-svgrepo-com.svg" />
        <h3 className="text-lg  max-w-xl font-semibold">Drag and drop your video or click to browser</h3>
        <span className="text-muted-foreground mb-8">maximun file size: 500MB Supported: MP4, MOV</span>
      <SelectFileButton />
      </div>
    </div>
  );
}
