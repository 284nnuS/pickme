import Head from 'next/head'
import env from '~/shared/env'
import { ChatBox } from '~/src/components'

function Index(props: { yourId: number; other: UserInfo }) {
   return (
      <>
         <Head>
            <title>PickMe</title>
         </Head>
         <div className="flex">
            <div className="h-screen w-72"></div>
            <ChatBox {...props} />
         </div>
      </>
   )
}

export async function getServerSideProps({ query, res }) {
   const { locals } = res
   const userId = +locals.session.userInfo.userId
   const otherId = +query.uid

   if (otherId === userId) {
      return
   }

   let data: UserInfo
   try {
      const result = await fetch(`${env.javaServerUrl}/user/basic/id/${otherId}`)
      data = (await result.json()).data
   } catch {
      //
   }

   return {
      props: {
         yourId: userId,
         other: data,
      },
   }
}

export default Index
