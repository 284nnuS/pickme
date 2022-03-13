import { Image } from '@mantine/core'
import Head from 'next/head'
import Link from 'next/link'
import { useEffect, useRef, useState } from 'react'
import { FaUserAlt } from 'react-icons/fa'
import { io } from 'socket.io-client'
import env from '~/shared/env'
import { ChatBox, MessageItem } from '~/src/components'

function Index({
   userInfo,
   other,
   preMessageList,
}: {
   userInfo: UserInfo
   other: UserInfo
   preMessageList: MessageItem[]
}) {
   const socket = io('/match')

   useEffect(() => {
      socket.open()
      socket.on('Remove in match list', (id: number) => {
         setMessageList((current) => {
            const idx = current.findIndex((e) => e.userId === id)
            current.splice(idx, 1)
            return [...current]
         })
         if (id === other.userId) setDeleted(true)
      })
   }, [])

   const linkToMatchedPageRef = useRef<HTMLAnchorElement>()

   const [deleted, setDeleted] = useState(false)

   const [messageList, setMessageList] = useState<MessageItem[]>(preMessageList)

   const updateCallBack = (newMessage: Message) => {
      const userId = newMessage.sender === userInfo.userId ? newMessage.receiver : newMessage.sender
      const index = messageList.findIndex((t) => t.userId === userId)
      if (index > -1)
         setMessageList((current: MessageItem[]) => {
            current[index] = {
               ...current[index],
               messageId: newMessage.messageId,
               time: newMessage.time,
               content: newMessage.content,
               isSender: userId === newMessage.receiver,
            }
            return [...current]
         })
      else
         setMessageList((current: MessageItem[]) => {
            current[index] = {
               ...current[index],
               messageId: newMessage.messageId,
               time: newMessage.time,
               content: newMessage.content,
               isSender: userId === newMessage.receiver,
            }
            return [...current]
         })
   }

   return (
      <>
         <Head>
            <title>Pickme | Chat with {other.name.split(' ')[0]}</title>
         </Head>
         <div className="flex w-screen h-screen overflow-hidden">
            <div className="w-[30rem] min-w-[30rem] h-screen z-50">
               <div className="w-full h-[6rem] bg-gradient-to-r from-[#2f494d] to-[#68bdc4] flex p-5 items-center gap-x-3">
                  <Image src={userInfo.avatar} radius={100} width={60} height={60} alt="" />
                  <div className="ml-2 text-2xl font-bold text-white">{userInfo.name}</div>
                  <div className="flex-grow"></div>
                  <button>
                     <div className="grid grid-cols-1 bg-white rounded-full w-14 h-14 place-items-center">
                        <div>
                           <FaUserAlt className="w-6 h-6 text-[#2f494d]" />
                        </div>
                     </div>
                  </button>
               </div>

               <div className="w-full h-[90%] bg-slate-100">
                  <div className="flex justify-around pt-3 pb-1">
                     <button
                        type="button"
                        className="w-24 text-xl font-semibold text-black border-b-2 border-b-slate-100 hover:border-b-slate-500"
                        onClick={() => linkToMatchedPageRef.current?.click()}
                     >
                        Matches
                     </button>
                     <button type="button" className="w-24 text-xl font-semibold text-black border-b-2 border-b-black">
                        Messages
                     </button>
                  </div>

                  <div className="flex flex-col pt-6 gap-y-3">
                     {messageList
                        .sort((a, b) => b.time - a.time)
                        .map((m) => (
                           <MessageItem key={m.userId} item={m} selected={m.userId === other.userId} />
                        ))}
                  </div>
               </div>
            </div>
            <div className="hidden">
               <Link href="/app" passHref>
                  <a ref={linkToMatchedPageRef}>App</a>
               </Link>
            </div>
            <div className="flex flex-col items-center justify-center flex-grow gap-y-8">
               <ChatBox
                  yourId={userInfo.userId}
                  other={other}
                  deleted={deleted}
                  updateCallBack={updateCallBack}
                  unmatchCallback={() =>
                     socket.emit('Unmatch', {
                        id: other.userId,
                        name: other.name,
                     })
                  }
               />
            </div>
         </div>
      </>
   )
}

export async function getServerSideProps({ query, res }) {
   const { locals } = res
   const userInfo: UserInfo = locals.session.userInfo
   const userId = userInfo.userId

   const otherId = +query.uid

   if (otherId === userId) {
      return
   }

   let other: UserInfo
   let messageList: MessageItem[]
   try {
      let result = await fetch(`${env.javaServerUrl}/matchStatus/${userId}/${otherId}`)
      const match = (await result.json()).data as boolean
      if (!match) {
         return {
            notFound: true,
         }
      }

      result = await fetch(`${env.javaServerUrl}/user/basic/id/${otherId}`)
      other = (await result.json()).data
      result = await fetch(`${env.javaServerUrl}/messageList/userId/${userId}`)
      messageList = (await result.json()).data
   } catch {
      //
   }

   return {
      props: {
         userInfo,
         other,
         preMessageList: messageList,
      },
   }
}

export default Index
