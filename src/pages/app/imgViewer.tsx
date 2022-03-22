import { Image } from '@mantine/core'
import axios from 'axios'
import classNames from 'classnames'
import Head from 'next/head'
import { useRouter } from 'next/router'
import { useState } from 'react'
import { VscChromeClose } from 'react-icons/vsc'
import { useKeyPressEvent } from 'react-use'
import env from '~/shared/env'

function SafeHydrate({ children }) {
   return <div suppressHydrationWarning>{typeof window === 'undefined' ? null : children}</div>
}

function ViewImage({
   userId,
   bucketName,
   images,
   currentPhotoName,
}: {
   userId: number
   bucketName: string
   images: File[]
   currentPhotoName: string
}) {
   const [index, setIndex] = useState(images.findIndex((el) => el.fileName === currentPhotoName))
   const img = images[index]

   const previousImage = () => setIndex(images.length - ((images.length - index) % images.length) - 1)
   const nextImage = () => setIndex((index + 1) % images.length)

   useKeyPressEvent('ArrowLeft', previousImage)
   useKeyPressEvent('ArrowRight', nextImage)
   useKeyPressEvent('Escape', () => router.back())
   const router = useRouter()

   const url =
      typeof window === 'undefined'
         ? null
         : `${window.location.origin}/api/restful/file/${userId}/${bucketName}/${img.fileName}`

   return (
      <>
         <Head>
            <title>Pickme | Image Viewer</title>
         </Head>
         <SafeHydrate>
            <div className="relative w-screen h-full overflow-hidden">
               <div
                  className="absolute top-0 bottom-0 left-0 right-0 transform scale-110 bg-center bg-cover -z-10 filter blur-2xl brightness-50 no-repeat"
                  style={{
                     backgroundImage: `url('${url}')`,
                  }}
               ></div>
               <button
                  className="absolute p-2 bg-white rounded-full top-2 left-4 hover:bg-slate-100"
                  onClick={() => router.back()}
               >
                  <VscChromeClose className="w-6 h-6 text-black" />
               </button>
               <div className="absolute left-10 right-10 top-10 bottom-24">
                  <Image
                     src={url}
                     alt=""
                     fit="contain"
                     className="h-full"
                     classNames={{
                        figure: 'w-full h-full',
                        imageWrapper: 'w-full h-full',
                     }}
                     styles={{
                        image: {
                           height: '100% !important',
                        },
                     }}
                  />
               </div>
               <div className="absolute bottom-0 left-0 right-0 top-auto flex items-center justify-center w-full h-24 gap-x-6">
                  {images.map((el, i) => (
                     <div key={i}>
                        <button
                           onClick={() => setIndex(i)}
                           className={classNames('filter', i === index ? 'brightness-100' : 'brightness-50')}
                        >
                           <Image
                              src={`/api/restful/file/${userId}/${bucketName}/${el.fileName}`}
                              radius={60}
                              fit="cover"
                              width={60}
                              height={60}
                              alt=""
                           />
                        </button>
                     </div>
                  ))}
               </div>
            </div>
         </SafeHydrate>
      </>
   )
}

export async function getServerSideProps({ query }) {
   const { profileId, bucketName, currentPhotoName } = query

   let images: File[]
   try {
      images = (await axios.get(`${env.javaServerUrl}/file/${profileId}/${bucketName}`)).data['data']
   } catch {
      return {
         notFound: true,
      }
   }

   return {
      props: {
         userId: profileId,
         bucketName,
         images,
         currentPhotoName: currentPhotoName ? currentPhotoName : images[0].fileName,
      },
   }
}

export default ViewImage
