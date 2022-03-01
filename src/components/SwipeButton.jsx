import React from 'react'
import { BsArrowRepeat, BsHeartFill } from 'react-icons/bs'
import { GrClose, GrStar } from 'react-icons/gr'

const SwipeButton = ({ handleRepeatBtn, handleCloseBtn, handleStarBtn, handleHeartBtn }) => {
   return (
      <div className="justify-evenly fixed bottom-[2vh]  w-full flex">
         <button className=" border-inherit border-4 p-[1vw] rounded-full " onClick={() => handleRepeatBtn()}>
            <BsArrowRepeat style={{ fontSize: '250%', color: 'rgb(255 255 255)' }} />
         </button>
         <button className=" border-red-500 border-4 p-[1vw] rounded-full " onClick={() => handleCloseBtn()}>
            <GrClose style={{ fontSize: '250%', color: 'rgb(239 68 68)' }} />
         </button>
         <button className=" border-green-500 border-4 p-[1vw] rounded-full " onClick={() => handleStarBtn()}>
            <GrStar style={{ fontSize: '250%', color: 'rgb(34 197 94)' }} />
         </button>
         <button className=" border-4 border-blue-600 p-[1vw] rounded-full " onClick={() => handleHeartBtn()}>
            <BsHeartFill style={{ fontSize: '250%', color: 'rgb(37 99 235)' }} />
         </button>
      </div>
   )
}

export default SwipeButton
