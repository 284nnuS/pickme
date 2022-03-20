import '~/src/styles/global.css'
import { SessionProvider } from 'next-auth/react'
import { MantineProvider } from '@mantine/core'
import { NotificationsProvider } from '@mantine/notifications'
import Head from 'next/head'

const App = ({ Component, pageProps: { session, ...pageProps } }) => {
   return (
      <>
         <Head>
            <meta name="viewport" content="width=device-width, minimum-scale=1, initial-scale=1" />
         </Head>
         <SessionProvider session={session}>
            <MantineProvider>
               <NotificationsProvider>
                  <Component {...pageProps} />{' '}
               </NotificationsProvider>
            </MantineProvider>
         </SessionProvider>
      </>
   )
}
export default App
