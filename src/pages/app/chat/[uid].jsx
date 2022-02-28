import Head from 'next/head'
import { ChatBox } from '../../../components'

function Index(props) {
   return (
      <>
         <Head>
            <title>App</title>
         </Head>
         {/* <div className="flex flex-col items-center justify-center w-screen h-screen">
            <div className="font-semibold text-blue-600">App</div>
            <button
               className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 
					font-semibold rounded-full text-sm px-5 py-2.5 text-center mr-2 mb-2 
					dark:focus:ring-blue-800"
               onClick={() => signOut()}
            >
               <span>Log Out</span>
            </button>
         </div> */}
         <ChatBox {...props} />
      </>
   )
}

export async function getServerSideProps({ query, res }) {
   const uid = +query.uid
   const { locals } = res
   return {
      props: {
         yourId: locals.session.user.id,
         otherId: uid,
         yourAvatar: locals.session.user.avatar,
         yourName: locals.session.user.name,
         otherName: 'John Smith',
      },
   }
}

export default Index
