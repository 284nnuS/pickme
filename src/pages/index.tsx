import Head from 'next/head'
import { useSession, signIn } from 'next-auth/react'
import { Image } from '@mantine/core'
import Link from 'next/link'
import { useRef } from 'react'

function Index() {
   const { data: session } = useSession()

   const linkToAppRef = useRef<HTMLAnchorElement>()

   return (
      <>
         <Head>
            <title>Home</title>
         </Head>
         <div
            className="relative w-full h-full bg-indigo-900 bg-no-repeat bg-cover"
            style={{
               backgroundImage: `url('/static/images/landingbg.jpg')`,
            }}
         >
            <div className="container relative z-10 w-screen px-6 mx-auto md:px-12 ">
               <div className="flex items-center justify-between ">
                  <Image src="/static/images/pickme.png" className="w-[20vh] h-[20vh]" alt="" />
                  <button
                     className="block h-[60px] w-[140px] bg-white hover:bg-gray-300 py-3 px-4 rounded-full text-lg text-gray-800 font-bold uppercase float-right"
                     onClick={() => {
                        if (!session) signIn()
                        else linkToAppRef.current?.click()
                     }}
                  >
                     Continue
                  </button>
               </div>
               <div className="hidden">
                  <Link href="/app" passHref>
                     <a ref={linkToAppRef}>App</a>
                  </Link>
               </div>
               <h1 className="ml-5 text-6xl font-bold leading-tight text-white sm:text-7xl ">Let find your friend</h1>
            </div>
         </div>
      </>
   )
}

export default Index
