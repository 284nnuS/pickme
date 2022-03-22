import { Image } from '@mantine/core'
import axios from 'axios'
import Head from 'next/head'
import Link from 'next/link'
import { useEffect, useRef, useState } from 'react'
import { FaUserAlt } from 'react-icons/fa'
import { io } from 'socket.io-client'
import env from '~/shared/env'
import { ChatBox, ConversationItem, SidebarHeader } from '~/src/components'
import Fuse from 'fuse.js'
import { useRouter } from 'next/router'

function Index({
   userProfile,
   currentConversation,
   preConversations,
}: {
   userProfile: UserProfile
   currentConversation: Conversation
   preConversations: Conversation[]
}) {
   const socket = io('/match', {
      forceNew: true,
      transports: ['websocket'],
      upgrade: false,
   })

   const [keywords, setKeywords] = useState('')

   const [conversations, setConversations] = useState<Conversation[]>(preConversations)

   const fuse = new Fuse(conversations, {
      keys: ['otherName', 'latestMessage'],
   })

   const result = keywords.length >= 3 ? fuse.search(keywords) : null

   useEffect(() => {
      socket.open()
      socket
         .on('match:remove', (otherId: number) => {
            setConversations((current) => {
               const idx = current.findIndex((e) => e.otherId === otherId)
               current.splice(idx, 1)
               return [...current]
            })
            if (otherId === currentConversation.otherId) setDeleted(true)
         })
         .on('connect', () => {
            /** */
         })
         .on('disconnect', () => {
            socket.removeAllListeners()
         })

      return () => {
         socket.disconnect()
      }
   }, [])

   const [deleted, setDeleted] = useState(false)

   const updateCallBack = (newMessage: Message) => {
      const index = conversations.findIndex((t) => t.conversationId === newMessage.conversationId)
      if (index > -1)
         setConversations((current) => {
            current[index] = {
               ...current[index],
               latestTime: newMessage.time,
               latestMessage: newMessage.content,
            }
            return [...current]
         })
   }

   const router = useRouter()

   const chatBoxRef = useRef<HTMLDivElement>()
   const conversationListRef = useRef<HTMLDivElement>()

   const scrollToChatBox = () => {
      chatBoxRef.current?.scrollIntoView({
         behavior: 'smooth',
      })
      router.events.off('routeChangeComplete', scrollToChatBox)
   }

   const scrollToConversationList = () => {
      conversationListRef.current?.scrollIntoView({
         behavior: 'smooth',
      })
      router.events.off('routeChangeComplete', scrollToConversationList)
   }

   return (
      <>
         <Head>
            <title>Pickme | Chat with {currentConversation.otherName.split(' ')[0]}</title>
         </Head>
         <div className="flex w-[200%] h-full overflow-hidden md:w-screen">
            <div
               className="w-screen min-w-screen md:w-[30rem] md:min-w-[30rem] h-full z-50 bg-slate-100"
               ref={conversationListRef}
            >
               <SidebarHeader userProfile={userProfile} tab="messages" />
               <div className="w-full px-6 pt-6">
                  <input
                     type="text"
                     value={keywords}
                     onChange={(e) => setKeywords(e.target.value)}
                     placeholder="Search conversations"
                     className="w-full px-6 py-3 transition-all border-b-2 border-transparent rounded-full outline-none focus:border-teal-500 bg-slate-200 focus:rounded-none focus:bg-transparent"
                  />
               </div>
               <div className="flex flex-col w-full pt-6 gap-y-3">
                  {!result &&
                     conversations
                        .sort((a, b) => b.latestTime - a.latestTime)
                        .map((m) => (
                           <ConversationItem
                              scrollCallback={() => scrollToChatBox()}
                              key={m.conversationId}
                              item={m}
                              selected={m.conversationId === currentConversation.conversationId}
                           />
                        ))}
                  {result && result.length > 0
                     ? result.map((m) => (
                          <ConversationItem
                             scrollCallback={() => scrollToChatBox()}
                             key={m.item.conversationId}
                             item={m.item}
                             selected={m.item.conversationId === currentConversation.conversationId}
                          />
                       ))
                     : result && (
                          <div className="mt-6 text-xl text-center text-bold text-slate-600">
                             No conversations found
                          </div>
                       )}
               </div>
            </div>
            <div className="flex flex-col items-center justify-center w-screen md:flex-grow gap-y-8" ref={chatBoxRef}>
               <ChatBox
                  key={currentConversation.conversationId}
                  scrollToConversationList={scrollToConversationList}
                  scrollToChatBox={scrollToChatBox}
                  yourProfile={userProfile}
                  deleted={deleted}
                  conversation={currentConversation}
                  updateCallBack={updateCallBack}
                  unMatchCallback={() => {
                     socket.emit('unmatch', {
                        userId: currentConversation.otherId,
                        name: currentConversation.otherName,
                     })
                     setConversations((current) => {
                        const idx = current.findIndex((e) => e.conversationId === currentConversation.conversationId)
                        current.splice(idx, 1)
                        return [...current]
                     })
                     setDeleted(true)
                  }}
               />
            </div>
         </div>
      </>
   )
}

export async function getServerSideProps({ query, res }) {
   const { locals } = res

   if (!locals.session) {
      return {
         notFound: true,
      }
   }

   const userInfo: UserInfo = locals.session.userInfo
   const userId = userInfo.userId

   const conversationId = +query.conversationId

   let userProfile: UserProfile
   let currentConversation: Conversation
   let preConversations: Conversation[]
   try {
      userProfile = (await axios.get(`${env.javaServerUrl}/profile/id/${userId}`)).data['data']
      preConversations = (await axios.get(`${env.javaServerUrl}/conversation/${userId}`)).data['data']
      currentConversation = preConversations.find((el) => el.conversationId === conversationId)
   } catch (err) {
      return {
         notFound: true,
      }
   }

   if (!currentConversation)
      return {
         notFound: true,
      }

   return {
      props: {
         userProfile,
         currentConversation,
         preConversations,
      },
   }
}

export default Index
