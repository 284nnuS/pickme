import { Dispatch, SetStateAction, useRef } from 'react'
import { BsPlus } from 'react-icons/bs'
import { MdKeyboardVoice } from 'react-icons/md'

function VoiceUpload({ voice, setVoice }: { voice: FileInfo; setVoice: Dispatch<SetStateAction<FileInfo>> }) {
   const inputRef = useRef<HTMLInputElement>()

   const handleFile = (file: globalThis.File, callback: (file: FileInfo) => void) => {
      const reader = new FileReader()
      reader.onload = (e) => {
         callback.call(null, {
            name: file.name,
            dataUrl: e.target.result,
         } as FileInfo)
      }
      reader.readAsDataURL(file)
   }

   return (
      <div>
         <label className="block mb-2 text-sm font-bold text-gray-900 dark:text-gray-300" id="user_audio">
            Profile Audio
         </label>
         <div className="relative p-2.5 flex items-center justify-between w-full">
            <MdKeyboardVoice className="w-5 h-5" />
            <input
               className="caret-transparent absolute top-0 bottom-0 left-0 right-0 w-full h-full bg-transparent border border-gray-300 rounded-lg px-12 py-2.5 bg-gray-50 focus:outline-none"
               value={voice.name}
               // eslint-disable-next-line @typescript-eslint/no-empty-function
               onChange={() => {}}
               placeholder="Select your voice"
               onKeyDown={() => false}
               onPaste={() => false}
               onMouseDown={() => false}
            ></input>
            <button
               type="button"
               className="z-50 bg-gray-800 rounded-full w-7 h-7"
               onClick={() => inputRef.current.click()}
            >
               <BsPlus className="w-full h-full text-white" />
            </button>
         </div>
         <input
            className="hidden"
            type="file"
            accept="audio/mpeg, audio/aac"
            ref={inputRef}
            onChange={(e) => handleFile(e.target.files[0], (ev) => setVoice(ev))}
         />
      </div>
   )
}

export default VoiceUpload
