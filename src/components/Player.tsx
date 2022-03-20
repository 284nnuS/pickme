import { useState, useEffect } from 'react'
import { BsFillVolumeDownFill, BsFillVolumeMuteFill } from 'react-icons/bs'
import { useKeyPressEvent } from 'react-use'

function Player({ voices, isFirst }: { voices: File[]; isFirst: boolean }) {
   const [audio, setAudio] = useState<HTMLAudioElement>(null)
   const [paused, setPaused] = useState(true)

   const [index, setIndex] = useState(voices.length - 1)

   const endAudio = () => {
      setPaused(true)
   }

   const toggle = () => setPaused((c) => !c)

   useKeyPressEvent('ArrowDown', isFirst && toggle)

   useEffect(() => {
      const file = voices[index]
      const newAudio = new Audio(
         `${window.location.origin}/api/restful/file/${file.userId}/${file.bucketName}/${file.fileName}`,
      )
      if (audio) audio.removeEventListener('ended', endAudio)
      newAudio.addEventListener('ended', endAudio)
      setAudio(newAudio)
      return () => {
         audio?.removeEventListener('ended', endAudio)
      }
   }, [index])

   useEffect(() => {
      if (paused) {
         audio?.pause()
         setIndex((c) => (c + 1) % voices.length)
      } else audio?.play()
   }, [paused])

   return (
      <button onClick={toggle}>
         {paused ? (
            <BsFillVolumeDownFill className="w-[40px] h-[40px] text-white" />
         ) : (
            <BsFillVolumeMuteFill className="w-[40px] h-[40px] text-white" />
         )}
      </button>
   )
}

export default Player
