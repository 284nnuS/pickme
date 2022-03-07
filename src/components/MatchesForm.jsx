import React from 'react'

const MatchesForm = ({ avt, name }) => {
   return (
      <div>
         <button
            type="button"
            style={{
               backgroundImage: `url(${avt})`,
            }}
            href="#"
            className="w-[84%] h-[10rem] rounded-xl bg-black bg-cover bg-center focus:ring-4 focus:ring-blue-300"
         >
            <div className="text-white font-medium text-xl mt-[7.7rem] ml-[0.5rem]">{name}</div>
         </button>
      </div>
   )
}

export default MatchesForm
