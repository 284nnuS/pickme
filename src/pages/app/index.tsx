import { MatchesItem, PickMeCard, Guide, NotificationBox, SidebarHeader } from '~/src/components'
import env from '~/shared/env'
import { useEffect, useRef, useState } from 'react'
import Head from 'next/head'
import axios from 'axios'
import { io } from 'socket.io-client'
import { useRouter } from 'next/router'
import { GiCardRandom } from 'react-icons/gi'
import { FaUserFriends } from 'react-icons/fa'
import classNames from 'classnames'

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

   const router = useRouter()

   const matchListRef = useRef<HTMLDivElement>()
   const cardViewerRef = useRef<HTMLDivElement>()

   const scrollToMatchList = () => {
      matchListRef.current?.scrollIntoView({
         behavior: 'smooth',
      })
      router.events.off('routeChangeComplete', scrollToMatchList)
   }

   const scrollToCardViewer = () => {
      cardViewerRef.current?.scrollIntoView({
         behavior: 'smooth',
      })
      router.events.off('routeChangeComplete', scrollToCardViewer)
   }

   const [currentTab, setCurrentTab] = useState('match')

   useEffect(() => {
      switch (currentTab) {
         case 'match':
            scrollToMatchList()
            break
         case 'card':
            scrollToCardViewer()
            break
      }
   }, [currentTab])

   return (
      <>
         <Head>
            <title>Pickme | Match</title>
         </Head>
         <div className="flex w-[200%] md:w-screen h-screen pb-16 md:pb-0 overflow-hidden">
            <div
               className="w-screen min-w-screen md:w-[30rem] md:min-w-[30rem] h-full z-50 bg-slate-100"
               ref={matchListRef}
            >
               <SidebarHeader userProfile={userProfile} tab="matches" />
               <div className="grid w-full grid-cols-2 pt-6 mx-2 md:grid-cols-3 md:mx-5 md:gap-x-1 gap-y-5">
                  {userProfiles.map((match) => (
                     <div key={match.userId} className="flex items-center justify-center w-full h-full">
                        <MatchesItem userId={match.userId} name={match.name} photos={match.photos} />
                     </div>
                  ))}
               </div>
            </div>
            <div
               className="flex flex-col items-center justify-center w-screen md:flex-grow md:gap-y-8"
               ref={cardViewerRef}
            >
               <div className="flex items-center w-full px-8 pt-2 pb-4">
                  <div className="flex-grow"></div>
                  <NotificationBox yourId={userProfile.userId} />
               </div>
               <PickMeCard defaultInterests={defaultInterests} socket={socket} init={init} />
               <Guide />
            </div>
         </div>
         <div className="fixed top-[calc(100vh-4rem)] left-0 right-0	 z-50 flex items-center h-16 bg-slate-200 md:hidden justify-evenly">
            {Object.values([
               {
                  children: (
                     <>
                        <FaUserFriends className={classNames('w-6 h-6', currentTab === 'match' && 'text-blue-600')} />
                        <div className={classNames(currentTab === 'match' && 'text-blue-600')}>Match List</div>
                     </>
                  ),
                  callback: () => setCurrentTab('match'),
               },
               {
                  children: (
                     <>
                        <GiCardRandom className={classNames('w-6 h-6', currentTab === 'card' && 'text-teal-600')} />
                        <div className={classNames(currentTab === 'card' && 'text-teal-600')}>Cards</div>
                     </>
                  ),
                  callback: () => setCurrentTab('card'),
               },
            ]).map((el, i) => {
               return (
                  <button key={i} onClick={el.callback} className="text-slate-500">
                     <div className="flex flex-col items-center">{el.children}</div>
                  </button>
               )
            })}
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
