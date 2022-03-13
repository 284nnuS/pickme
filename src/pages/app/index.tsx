import { MatchesItem, PickMeCard, Guide, NotificationBox } from '~/src/components'
import { FaUserAlt } from 'react-icons/fa'
import env from '~/shared/env'
import Link from 'next/link'
import { useEffect, useRef, useState } from 'react'
import Head from 'next/head'
import axios from 'axios'
import { io } from 'socket.io-client'

function Index({ userInfo, defaultInterests }: { userInfo: UserInfo; defaultInterests: InterestChip[] }) {
   const linkToMessengerPageRef = useRef<HTMLAnchorElement>()

   const [matchedUsers, setMatchedUser] = useState<MatchedUser[]>([])

   const socket = io('/match')
   const [init, setInit] = useState(false)

   const process = (users: MatchedUser[]) =>
      users.filter((v: MatchedUser, i: number, a: MatchedUser[]) => a.findIndex((t) => t.userId === v.userId) === i)

   useEffect(() => {
      socket.open()
      socket
         .on('Match list', (users: MatchedUser[]) => {
            setMatchedUser((current: MatchedUser[]) => process([...current, ...users]))
         })
         .on('New match', (user: MatchedUser) =>
            setMatchedUser((current: MatchedUser[]) => process([...current, user])),
         )
         .on('Remove in match list', (id: number) => {
            setMatchedUser((current) => {
               const idx = current.findIndex((e) => e.userId === id)
               current.splice(idx, 1)
               return [...current]
            })
         })
         .on('connect', () => setInit(true))
         .on('disconnect', () => {
            socket.removeAllListeners()
         })
   }, [])

   useEffect(() => {
      if (init) {
         socket.emit('Get match list')
      }
   }, [init])

   return (
      <>
         <Head>
            <title>Pickme | Match</title>
         </Head>
         <div className="flex w-screen h-screen overflow-hidden">
            <div className="w-[30rem] min-w-[30rem] h-screen z-50">
               <div className="w-full h-[6rem] bg-gradient-to-r from-[#2f494d] to-[#68bdc4] flex justify-between">
                  <div className="flex justify-around mt-5 ml-5">
                     <div
                        style={{
                           backgroundImage: `url('${userInfo.avatar}')`,
                        }}
                        className="bg-center bg-cover rounded-full w-14 h-14"
                     ></div>
                     <a href="#">
                        <div className="mt-2 ml-2 text-2xl font-medium text-white">{userInfo.name}</div>
                     </a>
                  </div>

                  <div className="mt-5 mr-5">
                     <button>
                        <div className="grid grid-cols-1 bg-white rounded-full w-14 h-14 place-items-center">
                           <div>
                              <FaUserAlt className="w-6 h-6 text-[#2f494d]" />
                           </div>
                        </div>
                     </button>
                  </div>
               </div>

               <div className="w-full h-full bg-slate-100">
                  <div className="flex justify-around pt-3 pb-1">
                     <button type="button" className="w-24 text-xl font-semibold text-black border-b-2 border-b-black">
                        Matches
                     </button>
                     <button
                        type="button"
                        className="w-24 text-xl font-semibold text-black border-b-2 border-b-slate-100 hover:border-b-slate-500"
                        onClick={() => matchedUsers.length > 0 && linkToMessengerPageRef.current?.click()}
                     >
                        Messages
                     </button>
                  </div>

                  <div className="grid grid-cols-3 pt-3 ml-5 gap-x-1 gap-y-5">
                     {matchedUsers.map((match) => (
                        <MatchesItem key={match.userId} {...match} />
                     ))}
                  </div>
               </div>
            </div>
            <div className="hidden">
               <Link href="/app/chat" passHref>
                  <a ref={linkToMessengerPageRef}>App</a>
               </Link>
            </div>
            <div className="relative flex flex-col items-center justify-center flex-grow gap-y-8">
               <div className="absolute top-4 right-8">
                  <NotificationBox yourId={userInfo.userId} />
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

   let defaultInterests: any[]
   try {
      const result = await axios.get(`${env.javaServerUrl}/interest`)
      defaultInterests = result.data['data']
   } catch {
      //
   }

   return {
      props: {
         userInfo,
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
