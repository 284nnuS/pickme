import './../styles/global.css'
import { SessionProvider } from 'next-auth/react'
import { useEffect } from 'react'

const App = ({ Component, pageProps: { session, ...pageProps } }) => {
   useEffect(() => import('flowbite'))

   return (
      <>
         <SessionProvider session={session}>
            <Component {...pageProps} />
         </SessionProvider>
      </>
   )
}
export default App
