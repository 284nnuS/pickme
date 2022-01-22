import Head from 'next/head'
import { useEffect, useState } from 'react'
import io from 'socket.io-client'
import { signOut } from 'next-auth/react'

function Index() {
   const [isConnecting, setConnecting] = useState(false)

   const sio = io()

   useEffect(() => {
      if (!isConnecting && !sio.connected) {
         sio.open()
         sio.on('message', (data) => {
            console.log(data)
         })
         setConnecting(true)
      }
   }, [isConnecting, sio])

   return (
      <>
         <Head>
            <title>App</title>
         </Head>
         <div className="flex flex-col justify-center items-center w-screen h-screen">
            <div className="text-blue-600 font-semibold">App</div>
            <button
               className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 
					font-semibold rounded-full text-sm px-5 py-2.5 text-center mr-2 mb-2 dark:bg-blue-600 
					dark:hover:bg-blue-700 dark:focus:ring-blue-800"
               onClick={() => signOut({ callbackUrl: process.env.NEXT_PUBLIC_URL })}
            >
               <span>Log Out</span>
            </button>
         </div>
      </>
   )
}

export default Index
