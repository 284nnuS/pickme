import Head from 'next/head'
import 'twin.macro'

function Index(props) {
   return (
      <>
         <Head>
            <title>Home</title>
         </Head>
         <div tw="flex justify-center items-center w-screen h-screen">
            <p tw="text-blue-500 text-5xl">{props.message}</p>
         </div>
      </>
   )
}

export const getServerSideProps = async () => {
   const res = await fetch(`http://localhost:3001/hello`)
   const json = await res.json()
   return {
      props: json,
   }
}

export default Index
