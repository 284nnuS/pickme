import Link from 'next/link'
import React from 'react'

function MatchesForm({ userId, name, images }: { userId: number; name: string; images: File[] }) {
   const img = images[Math.trunc(Math.random() * images.length)]
   return (
      <div className="relative">
         <Link href={`/app/profile/${userId}`} passHref>
            <a
               style={{
                  backgroundImage: `url(/api/restful/file/${userId}/${img.bucketName}/${img.fileName})`,
               }}
               className="w-32 aspect-[2/3] rounded-xl bg-black bg-cover bg-center focus:ring-4 focus:ring-blue-300 block"
            >
               <div className="absolute text-2xl font-semibold text-white left-2 bottom-2">{name.split(' ')[0]}</div>
            </a>
         </Link>
      </div>
   )
}

export default MatchesForm
