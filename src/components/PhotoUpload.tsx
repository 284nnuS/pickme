import { Image } from '@mantine/core'
import { Dispatch, SetStateAction, useRef } from 'react'
import { BsPlusCircleFill } from 'react-icons/bs'
import { MdOutlineClose } from 'react-icons/md'

function PhotoUpload({ photos, setPhotos }: { photos: MediaFile[]; setPhotos: Dispatch<SetStateAction<MediaFile[]>> }) {
   const inputRef = useRef<HTMLInputElement>()

   const handleFile = (file: globalThis.File, callback: (file: MediaFile) => void) => {
      const reader = new FileReader()
      reader.onload = (e) => {
         callback.call(null, {
            name: file.name,
            dataUrl: e.target.result,
         } as MediaFile)
      }
      reader.readAsDataURL(file)
   }

   return (
      <div>
         <label className="block mb-2 text-sm font-bold text-gray-900 dark:text-gray-400">Profile Photo</label>
         <div className="grid items-center grid-cols-3 mb-3 gap-x-5 gap-y-5">
            {Object.values(photos).map((photo, i) => {
               return (
                  <div key={i} className="flex items-center justify-center">
                     <div className="relative w-32 aspect-[2/3] border rounded-md bg-slate-100">
                        <Image src={photo.dataUrl} alt={`Image ${i}`} width={128} height={192} fit="contain" />
                        <button
                           className="absolute w-5 h-5 p-1 bg-red-600 rounded-full hover:bg-red-800 -right-2 -bottom-2"
                           onClick={() =>
                              setPhotos((currentPhotos) => {
                                 currentPhotos.splice(i, 1)
                                 return [...currentPhotos]
                              })
                           }
                        >
                           <MdOutlineClose className="w-full h-full text-white" />
                        </button>
                     </div>
                  </div>
               )
            })}
            <div className="flex items-center justify-center">
               <button
                  className="w-32 aspect-[2/3] border rounded-md bg-slate-100 z-1"
                  onClick={() => inputRef.current.click()}
               >
                  <BsPlusCircleFill className="w-10 h-10 m-auto text-slate-400" />
               </button>
               <input
                  type="file"
                  accept="image/png, image/jpeg"
                  className="hidden"
                  ref={inputRef}
                  onChange={(e) =>
                     handleFile(e.target.files[0], (ev) => setPhotos((currentPhotos) => [...currentPhotos, ev]))
                  }
               />
            </div>
         </div>
      </div>
   )
}

export default PhotoUpload
