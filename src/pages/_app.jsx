import './../styles/global.css'
import { SessionProvider } from 'next-auth/react'

const App = ({ Component, pageProps: { session, ...pageProps } }) => (
   <>
      <SessionProvider session={session}>
         <Component {...pageProps} />
      </SessionProvider>
   </>
)

export default App
