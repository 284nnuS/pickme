import React from 'react'
import { BsArrowRepeat, BsSuitHeartFill } from 'react-icons/bs'
import { GrStar } from 'react-icons/gr'
import { VscChromeClose } from 'react-icons/vsc'

const SwipeButton = ({ handleRepeatBtn, handleCloseBtn, handleStarBtn, handleHeartBtn }) => {
   return (
      <div className="justify-evenly fixed bottom-[2vh] absolute w-full flex">
         <button className=" border-yellow-400 border-2 p-[1vw] rounded-full " onClick={() => handleRepeatBtn()}>
            <BsArrowRepeat style={{ fontSize: '250%', color: 'rgb(250 204 21)' }} />
         </button>
         <button className=" border-red-500 border-2 p-[1vw] rounded-full " onClick={() => handleCloseBtn()}>
            <VscChromeClose style={{ fontSize: '250%', color: 'rgb(239 68 68)' }} />
         </button>
         <button className=" border-green-500 border-2 p-[1vw] rounded-full " onClick={() => handleStarBtn()}>
            <GrStar style={{ fontSize: '250%', color: 'rgb(34 197 94)' }} />
         </button>
         <button
            className=" border-2 border-blue-600 p-[1vw] rounded-full items-center justify-center"
            onClick={() => handleHeartBtn()}
         >
            <BsSuitHeartFill style={{ fontSize: '250%', color: 'rgb(37 99 235)' }} />
         </button>
      </div>
   )
}

export default SwipeButton
