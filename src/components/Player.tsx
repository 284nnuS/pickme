import { useState, useEffect } from 'react'
import { BsFillVolumeDownFill, BsFillVolumeMuteFill } from 'react-icons/bs'
const Player = ({ url }) => {
   const [audio, setAudio] = useState(null)
   const [paused, setPaused] = useState(true)

   useEffect(() => {
      setAudio(new Audio(url))
   }, [])

   useEffect(() => {
      if (paused) audio?.pause()
      else audio?.play()
   }, [paused])

   return (
      <button
         onClick={() => {
            setPaused(!paused)
         }}
      >
         {paused ? (
            <BsFillVolumeDownFill className="w-[40px] h-[40px] text-white" />
         ) : (
            <BsFillVolumeMuteFill className="w-[40px] h-[40px] text-white" />
         )}
      </button>
   )
}

export default Player
