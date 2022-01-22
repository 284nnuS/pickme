import Head from 'next/head'
import { useSession, signIn } from 'next-auth/react'

function Index() {
   const { data: session } = useSession()

   return (
      <>
         <Head>
            <title>Home</title>
         </Head>
         <div className="flex justify-center items-center w-screen h-screen">
            {!session && (
               <button
                  className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 
					font-semibold rounded-full text-sm px-5 py-2.5 text-center mr-2 mb-2 dark:bg-blue-600 
					dark:hover:bg-blue-700 dark:focus:ring-blue-800"
                  onClick={() => signIn()}
               >
                  Login
               </button>
            )}
         </div>
      </>
   )
}

export default Index
