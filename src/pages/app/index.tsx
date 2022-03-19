import { MatchesItem, PickMeCard, Guide, NotificationBox, SidebarHeader } from '~/src/components'
import env from '~/shared/env'
import { useEffect, useState } from 'react'
import Head from 'next/head'
import axios from 'axios'
import { io } from 'socket.io-client'

function Index({ userProfile, defaultInterests }: { userProfile: UserProfile; defaultInterests: InterestChip[] }) {
   const [userProfiles, setUserProfile] = useState<UserProfile[]>([])

   const socket = io('/match', {
      forceNew: true,
      upgrade: false,
      transports: ['websocket'],
   })
   const [init, setInit] = useState(false)

   const process = (users: UserProfile[]) =>
      users.filter((v: UserProfile, i: number, a: UserProfile[]) => a.findIndex((t) => t.userId === v.userId) === i)

   useEffect(() => {
      socket.open()
      socket
         .on('match:list', (users: UserProfile[]) => {
            setUserProfile((current: UserProfile[]) => process([...current, ...users]))
         })
         .on('match:new', (user: UserProfile) =>
            setUserProfile((current: UserProfile[]) => process([...current, user])),
         )
         .on('match:remove', (id: number) => {
            setUserProfile((current) => {
               const idx = current.findIndex((e) => e.userId === id)
               current.splice(idx, 1)
               return [...current]
            })
         })
         .on('connect', () => setInit(true))
         .on('disconnect', () => {
            socket.removeAllListeners()
         })

      return () => {
         socket.disconnect()
      }
   }, [])

   useEffect(() => {
      if (init) {
         socket.emit('matched:get')
      }
   }, [init])

   return (
      <>
         <Head>
            <title>Pickme | Match</title>
         </Head>
         <div className="flex w-screen h-screen overflow-hidden">
            <div className="w-[30rem] min-w-[30rem] h-screen z-50 bg-slate-100">
               <SidebarHeader userProfile={userProfile} tab="matches" />

               <div className="grid w-full grid-cols-3 pt-6 ml-5 gap-x-1 gap-y-5">
                  {userProfiles.map((match) => (
                     <MatchesItem key={match.userId} userId={match.userId} name={match.name} photos={match.photos} />
                  ))}
               </div>
            </div>
            <div className="relative flex flex-col items-center justify-center flex-grow gap-y-8">
               <div className="absolute top-4 right-8">
                  <NotificationBox yourId={userProfile.userId} />
               </div>
               <PickMeCard defaultInterests={defaultInterests} socket={socket} init={init} />
               <Guide />
            </div>
         </div>
      </>
   )
}

export async function getServerSideProps({ res }) {
   const { locals } = res

   if (!locals.session) {
      return {
         notFound: true,
      }
   }

   const userInfo: UserInfo = locals.session.userInfo
   const userId = userInfo.userId

   // eslint-disable-next-line @typescript-eslint/no-explicit-any
   let defaultInterests: any[]
   let userProfile: UserProfile
   try {
      userProfile = (await axios.get(`${env.javaServerUrl}/profile/id/${userId}`)).data['data']
      defaultInterests = (await axios.get(`${env.javaServerUrl}/interest`)).data['data']
   } catch (err) {
      return {
         notFound: true,
      }
   }

   return {
      props: {
         userProfile,
         defaultInterests: defaultInterests.map((el) => {
            return {
               name: el['interestName'],
               description: el['description'],
            }
         }),
      },
   }
}

export default Index
