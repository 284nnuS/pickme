import React from 'react'
import { TiArrowBack } from 'react-icons/ti'

const MessageForm = ({ avt, name, isSend, content }) => {
   return (
      <button
         type="button"
         className="pt-3 pb-3 flex justify-start hover:bg-slate-200 hover:rounded-2xl focus:bg-slate-300 focus:rounded-2xl"
      >
         <div
            style={{
               backgroundImage: `url(${avt})`,
            }}
            className="w-[7.5rem] h-[7.5rem] rounded-full bg-cover bg-center ml-5"
         ></div>
         <div className="ml-5 mt-6 relative">
            <div className="font-semibold text-2xl absolute left-0">{name}</div>
            <div className="flex justify-start mt-[2.3rem]">
               {isSend && (
                  <div>
                     <TiArrowBack className="w-7 h-7" />
                  </div>
               )}
               <div className="font-normal text-lg">{content}</div>
            </div>
         </div>
      </button>
   )
}

export default MessageForm
