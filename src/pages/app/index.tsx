import { MatchesItem, PickMeCard, Guide, NotificationBox } from '~/src/components'
import { FaUserAlt } from 'react-icons/fa'
import env from '~/shared/env'
import Link from 'next/link'
import { useEffect, useState } from 'react'
import Head from 'next/head'
import axios from 'axios'
import { io } from 'socket.io-client'
import { Image } from '@mantine/core'

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
         .on('Match list', (users: UserProfile[]) => {
            setUserProfile((current: UserProfile[]) => process([...current, ...users]))
         })
         .on('New match', (user: UserProfile) =>
            setUserProfile((current: UserProfile[]) => process([...current, user])),
         )
         .on('Remove match list', (id: number) => {
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
         socket.emit('Get matched users')
      }
   }, [init])

   return (
      <>
         <Head>
            <title>Pickme | Match</title>
         </Head>
         <div className="flex w-screen h-screen overflow-hidden">
            <div className="w-[30rem] min-w-[30rem] h-screen z-50">
               <div className="w-full h-[6rem] bg-gradient-to-r from-teal-600 to-cyan-600 flex p-5 items-center gap-x-3">
                  <Image src={userProfile.avatar} radius={100} width={60} height={60} alt="" />
                  <div className="ml-2 text-2xl font-bold text-white">{userProfile.name}</div>
                  <div className="flex-grow"></div>
                  <Link href={`/app/profile/${userProfile.userId}`} passHref>
                     <a className="grid grid-cols-1 bg-white rounded-full w-14 h-14 place-items-center">
                        <FaUserAlt className="w-6 h-6 text-teal-600" />
                     </a>
                  </Link>
               </div>

               <div className="w-full h-full bg-slate-100">
                  <div className="flex justify-around pt-3 pb-1">
                     <Link href="/app" passHref>
                        <a className="w-24 text-xl font-semibold text-center text-teal-700 no-underline border-b-2 border-b-teal-700">
                           Matches
                        </a>
                     </Link>
                     <Link href="/app/chat" passHref>
                        <a className="w-24 text-xl font-semibold text-center text-teal-800 no-underline border-b-2 border-b-transparent hover:border-b-teal-800">
                           Messages
                        </a>
                     </Link>
                  </div>

                  <div className="grid grid-cols-3 pt-6 ml-5 gap-x-1 gap-y-5">
                     {userProfiles.map((match) => (
                        <MatchesItem key={match.userId} userId={match.userId} name={match.name} images={match.images} />
                     ))}
                  </div>
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
