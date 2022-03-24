import Head from 'next/head'

function Index() {
   return (
      <>
         <Head>
            <title>Pickme | No Content 204</title>
         </Head>
         <div className="flex items-center justify-center w-screen h-full bg-gradient-to-r from-teal-600 to-blue-400">
            <div className="px-40 py-20 bg-white rounded-md shadow-xl">
               <div className="flex flex-col items-center">
                  <h1 className="font-bold text-9xl">
                     <span className="text-teal-600 ">2</span>
                     <span className="text-cyan-600 ">0</span>
                     <span className="text-blue-600 ">4</span>
                  </h1>

                  <h6 className="mb-2 text-2xl font-bold text-center text-slate-800 md:text-3xl">
                     <span className="text-red-500">Oops!</span> No Content
                  </h6>

                  <p className="mb-8 text-center text-slate-500 md:text-lg">
                     Sorry but currently you don&#39;t match with anyone
                  </p>

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

export async function getServerSideProps({ res }) {
   res.statusCode = 302
   res.setHeader('Location', `/manage/reports`)
   return { props: {} }
}

export default Index
