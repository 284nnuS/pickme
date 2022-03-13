import '~/src/styles/global.css'
import { SessionProvider } from 'next-auth/react'
import { MantineProvider } from '@mantine/core'
import { NotificationsProvider } from '@mantine/notifications'

const App = ({ Component, pageProps: { session, ...pageProps } }) => {
   return (
      <>
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
