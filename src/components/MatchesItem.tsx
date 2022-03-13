import React from 'react'

function MatchesForm({ userId, name, medias }: { userId: number; name: string; medias: Media[] }) {
   medias = medias.filter((el) => el.mediaType === 'image')
   const img = medias[Math.trunc(Math.random() * medias.length)].mediaName
   return (
      <div className="relative">
         <button
            type="button"
            style={{
               backgroundImage: `url(/api/restful/media/${userId}/${img})`,
            }}
            className="w-32 aspect-[2/3] rounded-xl bg-black bg-cover bg-center focus:ring-4 focus:ring-blue-300"
         >
            <div className="absolute text-2xl font-semibold text-white left-2 bottom-2">{name.split(' ')[0]}</div>
         </button>
      </div>
   )
}

export default MatchesForm
