import Head from 'next/head'
import { useSession, signIn, signUp } from 'next-auth/react'

function Index() {
   const { data: session } = useSession()

   return (
      <>
         <Head>
            <title>Home</title>
         </Head>
         <div className="bg-indigo-900 relative overflow-hidden h-screen ">
            <img src="/static/images/6293645.jpg" className="absolute h-full w-full object-cover" />

            <div className="container mx-auto px-6 md:px-12 relative z-10  w-screen  ">
               <div className="flex justify-between items-center ">
                  <img src="/static/images/Pickme (2).png" className="w-[20vh] h-[20vh]" />
                  <button
                     className="block h-[60px] w-[140px] bg-white hover:bg-gray-100 py-3 px-4 rounded-full text-lg text-gray-800 font-bold uppercase  float-right"
                     onClick={() => signIn()}
                  >
                     Continue
                  </button>
               </div>
               <h1 className="font-bold text-6xl sm:text-7xl text-white leading-tight ml-5 ">Let find your friend</h1>
            </div>
         </div>
      </>
   )
}

export default Index
