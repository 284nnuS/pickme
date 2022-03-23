import Head from 'next/head'
import { useRouter } from 'next/router'
import React from 'react'

export default function NotFound() {
   const router = useRouter()

   const info = {
      code: 0,
      error: '',
      details: '',
   }

   switch (router.query['error']) {
      case 'AccessDenied':
         info.code = 403
         info.error = 'Access Denied'
         info.details = "Your account isn't allowed to access this website"
         break
   }

   return (
      <>
         <Head>
            <title>Pickme | Error {info.code}</title>
         </Head>
         <div className="flex items-center justify-center w-screen h-full bg-gradient-to-r from-teal-600 to-blue-400">
            <div className="px-40 py-20 bg-white rounded-md shadow-xl">
               <div className="flex flex-col items-center">
                  <h1 className="font-bold text-9xl">
                     <span className="text-teal-600 ">{Math.trunc(info.code / 100)}</span>
                     <span className="text-cyan-600 ">{Math.trunc((info.code % 100) / 10)}</span>
                     <span className="text-blue-600 ">{info.code % 10}</span>
                  </h1>

                  <h6 className="mb-2 text-2xl font-bold text-center text-slate-800 md:text-3xl">
                     <span className="text-red-500">Oops!</span> {info.error}
                  </h6>

                  <p className="mb-8 text-center text-slate-500 md:text-lg">{info.details}</p>

                  <button
                     className="px-6 py-2 text-sm font-semibold text-teal-800 bg-teal-100 rounded-full hover:bg-teal-200"
                     onClick={() => window.history.back()}
                  >
                     Take me back
                  </button>
               </div>
            </div>
         </div>
      </>
   )
}
