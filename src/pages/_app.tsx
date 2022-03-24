import '~/src/styles/global.css'
import { SessionProvider } from 'next-auth/react'
import { MantineProvider } from '@mantine/core'
import { NotificationsProvider } from '@mantine/notifications'
import Head from 'next/head'
import { motion } from 'framer-motion'

const App = ({ Component, pageProps: { session, ...pageProps }, router }) => {
   return (
      <>
         <Head>
            <meta name="viewport" content="width=device-width, minimum-scale=1, initial-scale=1" />
         </Head>
         <SessionProvider session={session}>
            <MantineProvider>
               <NotificationsProvider>
                  <motion.div
                     key={router.route}
                     initial="pageInitial"
                     animate="pageAnimate"
                     variants={{
                        pageInitial: {
                           opacity: 0,
                        },
                        pageAnimate: {
                           opacity: 1,
                        },
                     }}
                  >
                     <Component {...pageProps} />
                  </motion.div>
               </NotificationsProvider>
            </MantineProvider>
         </SessionProvider>
      </>
   )
}
export default App
