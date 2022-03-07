import Head from 'next/head'
import { useSession, signIn } from 'next-auth/react'
import Guide from '../components/Guide'
import MatchesList from '../components/MatchesList'

function Index() {
   const { data: session } = useSession()

   return (
      <>
         <MatchesList />
      </>
   )
}

export default Index
