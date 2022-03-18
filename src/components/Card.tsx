import { Image } from '@mantine/core'
import { useRouter } from 'next/router'
import { createRef, RefObject, useState } from 'react'
import { useKeyPressEvent } from 'react-use'
import ChipsInCardtsx from './ChipsInCard'

function Card({
   age,
   userId,
   name,
   defaultInterests,
   bio,
   images,
   interests,
   isFirst,
   onNavigate,
}: {
   age: number
   userId: number
   name: string
   defaultInterests: InterestChip[]
   bio: string
   images: File[]
   interests: string[]
   isFirst: boolean
   onNavigate: () => void
}) {
   const refs = images.reduce((acc, val, i) => {
      acc[i] = createRef<HTMLDivElement>()
      return acc
   }, {} as Record<number, RefObject<HTMLDivElement>>)

   const [currentImage, setCurrentImage] = useState(0)

   const scrollToImage = (i: number) => {
      setCurrentImage(i)
      refs[i].current.scrollIntoView({
         behavior: 'smooth',
         block: 'nearest',
         inline: 'start',
      })
   }

   const nextImage = () => {
      if (currentImage >= images.length - 1) scrollToImage(0)
      else scrollToImage(currentImage + 1)
   }

   const router = useRouter()

   useKeyPressEvent(' ', isFirst && nextImage)

   const [dragging, setDragging] = useState(false)

   return (
      <div
         className="relative flex flex-col justify-center w-full h-full bg-slate-700 pointer rounded-2xl"
         onMouseDown={() => setDragging(false)}
         onMouseMove={() => setDragging(true)}
         onMouseUp={() => {
            if (!dragging) {
               onNavigate()
               router.push(
                  `/app/photo/?profileId=${userId}&bucketName=${images[currentImage].bucketName}&currentPhotoName=${images[currentImage].fileName}`,
               )
            }
         }}
      >
         <div className="w-full h-full carousel rounded-2xl">
            {images.map((img, i) => (
               <Image
                  key={i}
                  ref={refs[i]}
                  src={`${window.location.origin}/api/restful/file/${userId}/photo/${img.fileName}`}
                  alt={name}
                  fit="cover"
                  radius={15}
                  className="flex-shrink-0 w-full h-full "
                  classNames={{
                     figure: 'w-full h-full',
                     imageWrapper: 'w-full h-full',
                  }}
                  styles={{
                     image: {
                        height: '100% !important',
                     },
                  }}
               />
            ))}
         </div>
         {/* <div className="absolute top-2 left-2">
            {voice && (
               <Player
                  url={`${window.location.origin}/api/restful/media/${userId}/${voice.mediaName}`}
                  isFirst={isFirst}
               />
            )}
         </div> */}
         <div className="absolute bottom-0 w-full rounded-b-2xl bg-gradient-to-t from-black h-1/2">
            <div className="absolute bottom-0 flex flex-col p-4 text-left pb-36 gap-y-2">
               <div className="flex items-end gap-x-4">
                  <h3 className="font-sans text-4xl font-black text-white">{name}</h3>
                  <h5 className="font-sans text-2xl font-black text-white">{age}</h5>
               </div>
               <ChipsInCardtsx values={defaultInterests.filter((el) => interests.indexOf(el.name) > -1)} />
               <p className="w-full font-sans text-xl text-white break-words">{bio}</p>
            </div>
         </div>
      </div>
   )
}

export default Card
