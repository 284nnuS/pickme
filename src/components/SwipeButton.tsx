import React from 'react'
import { BsArrowRepeat, BsSuitHeartFill } from 'react-icons/bs'
import { GrStar } from 'react-icons/gr'
import { VscChromeClose } from 'react-icons/vsc'

const SwipeButton = ({ handleRepeatBtn, handleCloseBtn, handleStarBtn, handleHeartBtn }) => {
   return (
      <div className="absolute z-10 grid w-full grid-cols-4 gap-3 px-3 bottom-10">
         <button
            className="border-yellow-400 border-2 p-[1vw] rounded-full aspect-[1/1] flex items-center justify-center hover:bg-white/20"
            onClick={() => handleRepeatBtn()}
            onKeyDown={(e) => e.preventDefault()}
         >
            <BsArrowRepeat style={{ fontSize: '250%', color: 'rgb(250 204 21)' }} />
         </button>
         <button
            className="border-red-500 border-2 p-[1vw] rounded-full aspect-square flex items-center justify-center hover:bg-white/20"
            onClick={() => handleCloseBtn()}
            onKeyDown={(e) => e.preventDefault()}
         >
            <VscChromeClose style={{ fontSize: '250%', color: 'rgb(239 68 68)' }} />
         </button>
         <button
            className="border-green-500 border-2 p-[1vw] rounded-full aspect-square flex items-center justify-center hover:bg-white/20"
            onClick={() => handleStarBtn()}
            onKeyDown={(e) => e.preventDefault()}
         >
            <GrStar style={{ fontSize: '250%', color: 'rgb(34 197 94)' }} />
         </button>
         <button
            className="border-2 border-blue-600 p-[1vw] rounded-full aspect-square flex items-center justify-center hover:bg-white/20"
            onClick={() => handleHeartBtn()}
            onKeyDown={(e) => e.preventDefault()}
         >
            <BsSuitHeartFill style={{ fontSize: '250%', color: 'rgb(37 99 235)' }} />
         </button>
      </div>
   )
}

export default SwipeButton
