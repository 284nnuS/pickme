import React, { useState } from 'react'
import { BsArrowRight, BsArrowLeft, BsArrowUp, BsArrowDown } from 'react-icons/bs'

function Guide() {
   const [onShow, setShow] = useState(true)
   const [onHide, setHide] = useState(false)

   const handleClickHide = () => {
      setShow(false)
      setHide(true)
   }

   const handleClickShow = () => {
      setShow(true)
      setHide(false)
   }

   return (
      <div className="flex gap-10 ml-[45rem] mt-[55rem]">
         {onHide && (
            <div>
               <button
                  type="button"
                  className="w-20 h-10 rounded-3xl font-semibold bg-gray-400 text-white"
                  onClick={handleClickShow}
               >
                  SHOW
               </button>
            </div>
         )}

         {onShow && (
            <div className="flex gap-10">
               <button
                  type="button"
                  className="w-20 h-10 rounded-3xl font-semibold bg-gray-400 text-white"
                  onClick={handleClickHide}
               >
                  HIDE
               </button>

               <div className="flex gap-2">
                  <div>
                     <BsArrowLeft className="w-10 h-10 text-2xl rounded-lg text-gray-400 border-2 border-gray-400" />
                  </div>
                  <div className="flex flex-col justify-center text-center font-semibold text-gray-400">NOPE</div>
               </div>

               <div className="flex gap-2">
                  <div>
                     <BsArrowRight className="w-10 h-10 text-2xl rounded-lg text-gray-400 border-2 border-gray-400" />
                  </div>
                  <div className="flex flex-col justify-center text-center font-semibold text-gray-400">LIKE</div>
               </div>

               <div className="flex gap-2">
                  <div>
                     <BsArrowUp className="w-10 h-10 text-2xl rounded-lg text-gray-400 border-2 border-gray-400" />
                  </div>
                  <div className="flex flex-col justify-center text-center font-semibold text-gray-400">
                     OPEN PROFILE
                  </div>
               </div>

               <div className="flex gap-2">
                  <div>
                     <BsArrowDown className="w-10 h-10 text-2xl rounded-lg text-gray-400 border-2 border-gray-400" />
                  </div>
                  <div className="flex flex-col justify-center text-center font-semibold text-gray-400">
                     CLOSE PROFILE
                  </div>
               </div>

               <div className="flex gap-2">
                  <div className="w-28 h-10 rounded-xl border-2 border-gray-400"></div>
                  <div className="flex flex-col justify-center text-center font-semibold text-gray-400">NEXT PHOTO</div>
               </div>
            </div>
         )}
      </div>
   )
}

export default Guide
