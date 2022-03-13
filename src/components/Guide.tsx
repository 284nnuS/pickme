import React, { useState } from 'react'
import { BsArrowRight, BsArrowLeft, BsArrowUp, BsArrowDown } from 'react-icons/bs'

function Guide() {
   const [onShow, setShow] = useState(true)

   return (
      <div className="flex select-none gap-x-10">
         {onShow || (
            <div>
               <button
                  type="button"
                  className="w-20 h-10 font-semibold text-white bg-gray-400 rounded-3xl"
                  onClick={() => setShow(true)}
               >
                  SHOW
               </button>
            </div>
         )}

         {onShow && (
            <div className="flex flex-wrap justify-center w-full gap-10 gap-y-3">
               <button
                  type="button"
                  className="w-20 h-10 font-semibold text-white bg-gray-400 rounded-3xl"
                  onClick={() => setShow(false)}
               >
                  HIDE
               </button>

               <div className="flex gap-2">
                  <div>
                     <BsArrowLeft className="w-10 h-10 text-2xl text-gray-400 border-2 border-gray-400 rounded-lg" />
                  </div>
                  <div className="flex flex-col justify-center font-semibold text-center text-gray-400">NOPE</div>
               </div>

               <div className="flex gap-2">
                  <div>
                     <BsArrowRight className="w-10 h-10 text-2xl text-gray-400 border-2 border-gray-400 rounded-lg" />
                  </div>
                  <div className="flex flex-col justify-center font-semibold text-center text-gray-400">LIKE</div>
               </div>

               <div className="flex gap-2">
                  <div>
                     <BsArrowUp className="w-10 h-10 text-2xl text-gray-400 border-2 border-gray-400 rounded-lg" />
                  </div>
                  <div className="flex flex-col justify-center font-semibold text-center text-gray-400">SUPER LIKE</div>
               </div>

               <div className="flex gap-2">
                  <div>
                     <BsArrowDown className="w-10 h-10 text-2xl text-gray-400 border-2 border-gray-400 rounded-lg" />
                  </div>
                  <div className="flex flex-col justify-center font-semibold text-center text-gray-400">
                     PLAY/PAUSE VOICE
                  </div>
               </div>

               <div className="flex gap-2">
                  <div className="h-10 border-2 border-gray-400 w-28 rounded-xl"></div>
                  <div className="flex flex-col justify-center font-semibold text-center text-gray-400">NEXT PHOTO</div>
               </div>
            </div>
         )}
      </div>
   )
}

export default Guide
