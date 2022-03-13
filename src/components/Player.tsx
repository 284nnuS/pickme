import { useState, useEffect } from 'react'
import { BsFillVolumeDownFill, BsFillVolumeMuteFill } from 'react-icons/bs'
import { useKeyPressEvent } from 'react-use'
const Player = ({ url, isFirst }: { url: string; isFirst: boolean }) => {
   const [audio, setAudio] = useState<HTMLAudioElement>(null)
   const [paused, setPaused] = useState(true)

   const endAudio = () => {
      setPaused(true)
   }

   const toggle = () => setPaused((c) => !c)

   useKeyPressEvent('ArrowDown', isFirst && toggle)

   useEffect(() => {
      const audio = new Audio(url)
      audio.addEventListener('ended', endAudio)
      setAudio(audio)
      return () => {
         audio.removeEventListener('ended', endAudio)
      }
   }, [])

   useEffect(() => {
      if (paused) audio?.pause()
      else audio?.play()
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
