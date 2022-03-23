import { useState, useEffect, useRef, useCallback, FormEvent } from 'react'
import classNames from 'classnames'
import { MdEmojiEmotions, MdSend } from 'react-icons/md'
import { io, Socket } from 'socket.io-client'
import { useThrottle, useThrottleCallback } from '@react-hook/throttle'
import { ReactSelect } from '.'
import { Avatar, Image, Modal, Popover, Tooltip } from '@mantine/core'
import { AiOutlineRollback } from 'react-icons/ai'
import Link from 'next/link'
import { IEmojiPickerProps } from 'emoji-picker-react'
import dynamic from 'next/dynamic'
import { useMedia, useScroll } from 'react-use'
import NotificationBox from './NotificationBox'
import MessageSearchBox from './MessageSearchBox'
import { IoMdArrowBack } from 'react-icons/io'

const EmojiPickerNoSSRWrapper = dynamic<IEmojiPickerProps>(() => import('emoji-picker-react'), {
   ssr: false,
   loading: () => <p>Loading ...</p>,
})

function ChatBox({
   scrollToConversationList,
   scrollToChatBox,
   conversation,
   yourProfile,
   deleted,
   updateCallBack,
   unMatchCallback,
}: {
   scrollToConversationList: () => void
   scrollToChatBox: () => void
   conversation: Conversation
   yourProfile: UserProfile
   deleted: boolean
   updateCallBack: (message: Message) => void
   unMatchCallback: (conversationId: number) => void
}) {
   const socket: Socket = io('/chat', {
      query: {
         conversationId: conversation.conversationId,
      },
      forceNew: true,
      transports: ['websocket'],
      upgrade: false,
   })

   const [messageList, setMessageList] = useState<Message[]>([])
   const [init, setInit] = useState(false)
   const [canLoadMore, setCanLoadMore] = useThrottle(false, 1, true)
   const [messageContent, setMessageContent] = useState('')
   const [openedPicker, setOpenedPicker] = useState(false)
   const [disableSpinner, setDisableSpinner] = useState(false)
   const [otherIsTyping, setOtherIsTyping] = useState(false)

   const [isTyping, setTyping] = useState(false)

   const process = (messages: Message[]) =>
      messages
         .filter((v: Message, i: number, a: Message[]) => a.findIndex((t) => t.messageId === v.messageId) === i)
         .filter((el: Message) => el.conversationId === conversation.conversationId)
         .sort((a: Message, b: Message) => b.time - a.time)

   useEffect(() => {
      socket.open()
      socket
         .on('messages', (messages: Message[]) => {
            if (messages && messages.length > 0) {
               setCanLoadMore(true)
               setMessageList((currentMessages) => process([...currentMessages, ...messages]))
            } else setDisableSpinner(true)
         })
         .on('message:new', (newMessage: Message) => {
            setMessageList((currentMessages) => process([newMessage, ...currentMessages]))
            updateCallBack(newMessage)
         })
         .on('message:react', (message: Message) =>
            setMessageList((current) => {
               const index = current.findIndex((t) => t.messageId === message.messageId)
               current[index].react = message.react
               return [...current]
            }),
         )
         .on('message:delete', (message: Message) =>
            setMessageList((current) => {
               const index = current.findIndex((t) => t.messageId === message.messageId)
               current[index].content = null
               updateCallBack(current[index])
               return [...current]
            }),
         )
         .on('message:typing', (state: boolean) => setOtherIsTyping(state))
         .on('connect', () => {
            setInit(true)
            scrollToChatBox()
         })
         .on('disconnect', () => {
            socket.removeAllListeners()
         })

      return () => {
         socket.disconnect()
      }
   }, [])

   useEffect(() => {
      if (!init) return

      if (messageContent.length === 0) {
         socket.emit('message:typing', false)
         setTyping(false)
         return
      }

      if (!isTyping) {
         socket.emit('message:typing', true)
         setTyping(true)
      }

      const delayDebounceFn = setTimeout(() => {
         socket.emit('message:typing', false)
         setTyping(false)
      }, 3000)

      return () => clearTimeout(delayDebounceFn)
   }, [messageContent])

   useEffect(() => {
      if (init) {
         const now = new Date()
         const currentTime = now.getTime() - now.getTimezoneOffset() * 60000
         socket.emit('message:getMore', currentTime)
         scrollRef.current.scrollTop = scrollRef.current.scrollHeight
      }
   }, [init])

   const scrollRef = useRef<HTMLUListElement>(null)
   const { y: scrollY } = useScroll(scrollRef)

   const handleScroll = useCallback(
      () => init && socket.emit('message:get+', messageList[messageList.length - 1].time),
      [messageList],
   )
   const throttledHandleScroll = useThrottleCallback(handleScroll, 0.5, true)

   useEffect(() => {
      if (canLoadMore && scrollY <= scrollRef.current?.lastChild['offsetTop']) throttledHandleScroll()
   }, [scrollY])

   const inputBoxRef = useRef<HTMLInputElement>()

   const sendMessage = (e: FormEvent) => {
      if (!init) return

      socket.emit('message:send', messageContent)
      setMessageContent('')
      e.preventDefault()
   }

   const abbreviate = (fullName: string) => {
      const words = fullName.split(' ')
      return (words[0][0] + (words.length > 1 ? '.' + words[1][0] : '')).toUpperCase()
   }

   const abbreviateName = abbreviate(conversation.otherName)

   const isWide = useMedia('(min-width: 768px)', true)
   const [unmatchModalOpened, setUnmatchModalOpened] = useState(false)

   return (
      <div className="w-full h-full overflow-hidden">
         <div className="flex items-center h-16 px-2 py-2 border-b-2 md:px-6 gap-x-2 md:gap-x-5 border-slate-200">
            <button className="w-7 h-7 md:hidden" onClick={() => scrollToConversationList()}>
               <IoMdArrowBack className="w-full h-full text-emerald-500" />
            </button>
            {!deleted && conversation.otherAvatar ? (
               <Avatar
                  src={conversation.otherAvatar}
                  radius={100}
                  alt={conversation.otherName}
                  size={isWide ? 48 : 36}
               />
            ) : (
               <p className="w-12 h-12 text-lg font-bold text-center text-white bg-teal-600 rounded-full leading-[3rem]">
                  {abbreviateName}
               </p>
            )}
            <Link href={`/app/profile/${conversation.otherId}`} passHref>
               <a className={classNames('text-lg font-semibold', deleted && 'line-through')}>
                  {conversation.otherName}
               </a>
            </Link>
            <div className="flex-grow"></div>
            <MessageSearchBox
               yourAvatar={yourProfile.avatar}
               conversation={conversation}
               messages={messageList}
               scrollToMessage={(index) =>
                  scrollRef.current?.children[index].scrollIntoView({
                     behavior: 'smooth',
                     block: 'center',
                     inline: 'center',
                  })
               }
            />
            <NotificationBox yourId={yourProfile.userId} />
            {deleted || (
               <>
                  <button
                     className="p-1.5 text-sm font-bold text-teal-500 uppercase bg-white border-2 border-teal-500 rounded-full hover:bg-teal-500 hover:text-white"
                     onClick={() => setUnmatchModalOpened(true)}
                  >
                     Unmatch
                  </button>
                  <Modal
                     opened={unmatchModalOpened}
                     onClose={() => setUnmatchModalOpened(false)}
                     centered
                     size={400}
                     title="Unmatch"
                     radius={20}
                     classNames={{
                        title: 'text-xl font-semibold',
                     }}
                  >
                     <div className="flex flex-col items-center w-full px-3 gap-y-3">
                        <div className="w-full">
                           Do you want to unmatch with
                           <span className="font-semibold"> {conversation.otherName}</span>?
                           <br />
                           This aciton won&#39;t be revertable
                        </div>
                        <div className="flex justify-around w-full">
                           <button
                              className="w-32 h-10 text-lg font-semibold text-white bg-red-700 rounded-md hover:bg-red-500 focus:outline-none"
                              onClick={() => unMatchCallback(conversation.conversationId)}
                           >
                              Yes
                           </button>
                           <button
                              className="w-32 h-10 text-lg font-semibold border-2 rounded-md text-slate-500 border-slate-500 hover:bg-slate-500 hover:text-white focus:outline-none"
                              onClick={() => setUnmatchModalOpened(false)}
                           >
                              No
                           </button>
                        </div>
                     </div>
                  </Modal>
               </>
            )}
         </div>
         <ul className="flex flex-col-reverse p-6 overflow-y-auto h-[calc(100%-8rem)] mb-2 relative" ref={scrollRef}>
            {!deleted ? (
               Object.values(messageList).map((el, i, list) => {
                  const youIsSender = yourProfile.userId === el.sender
                  const currentTime = new Date()
                  const nextMessageTime = i < list.length - 1 && new Date(list[i + 1].time)
                  const currentMessageTime = new Date(el.time)
                  const nextMessageIsMoreThanADay =
                     nextMessageTime &&
                     (currentTime.getTime() - nextMessageTime.getTime()) / (1000 * 60 * 60 * 24) > 1.0
                  const currentMessageIsMoreThanADay =
                     (currentTime.getTime() - currentMessageTime.getTime()) / (1000 * 60 * 60 * 24) > 1.0
                  const isANewDay = nextMessageTime && currentMessageTime.getDay() !== nextMessageTime.getDay()

                  return (
                     <li key={el.messageId}>
                        {(i === list.length - 1 || (nextMessageIsMoreThanADay && isANewDay)) && (
                           <div className="w-full py-6 text-sm font-semibold text-center text-slate-400">
                              {currentMessageTime.toLocaleDateString()}
                           </div>
                        )}
                        <div className={classNames('flex relative', youIsSender && 'flex-row-reverse')}>
                           {youIsSender ||
                              (conversation.otherAvatar ? (
                                 <Image
                                    src={conversation.otherAvatar}
                                    alt="Avatar"
                                    className={
                                       i === 0 || list[i - 1].sender !== el.sender
                                          ? 'absolute left-0 bottom-0 top-auto'
                                          : 'hidden'
                                    }
                                    radius={100}
                                    width={40}
                                    height={40}
                                 />
                              ) : (
                                 <p
                                    className={
                                       i > 0 && list[i - 1].sender !== el.sender
                                          ? 'absolute left-0 bottom-0 top-auto w-10 h-10 font-semibold leading-10 text-center text-white bg-teal-600 rounded-full'
                                          : 'hidden'
                                    }
                                 >
                                    {abbreviateName}
                                 </p>
                              ))}
                           <div
                              className={classNames(
                                 'max-w-[60%] ml-14 relative group',
                                 youIsSender ? 'pl-10' : 'pr-10',
                                 i < list.length - 1 && list[i + 1].sender !== el.sender ? 'mt-12' : 'mt-5',
                              )}
                           >
                              <Tooltip
                                 label={
                                    (currentMessageIsMoreThanADay
                                       ? currentMessageTime.toLocaleDateString() + ' '
                                       : '') + currentMessageTime.toLocaleTimeString()
                                 }
                              >
                                 <div
                                    className={classNames(
                                       el.content
                                          ? youIsSender
                                             ? 'bg-cyan-600 text-white rounded-br-none'
                                             : 'bg-slate-300 text-black rounded-bl-none'
                                          : 'bg-white-300 border-2 border-slate-300 text-slate-300 italic',
                                       'rounded-3xl p-3 block relative',
                                    )}
                                 >
                                    {el.content ? el.content : 'This message is removed'}
                                    {el.content && el.react !== 'none' && (
                                       <div className="absolute flex items-center justify-center w-6 h-6 bg-white border rounded-full -right-2 -bottom-2 z-1">
                                          <Image
                                             src={`/static/images/${el.react}16.png`}
                                             width={16}
                                             height={16}
                                             radius={100}
                                             alt={el.react}
                                          />
                                       </div>
                                    )}
                                 </div>
                              </Tooltip>
                              {el.content &&
                                 (youIsSender ? (
                                    <div className="absolute top-0 bottom-0 items-center hidden group-hover:flex -left-0">
                                       <button
                                          className="relative p-1 bg-white rounded-full w-7 h-7 hover:bg-slate-300"
                                          onClick={() => {
                                             socket.emit('message:delete', el.messageId)
                                          }}
                                       >
                                          <AiOutlineRollback className="w-full h-full text-gray-500" />
                                       </button>
                                    </div>
                                 ) : (
                                    <ReactSelect socket={socket} message={el} init={init} />
                                 ))}
                           </div>
                        </div>
                     </li>
                  )
               })
            ) : (
               <div className="absolute top-0 bottom-0 left-0 right-0 flex items-center justify-center text-xl font-semibold text-slate-500">
                  You cannot chat with {abbreviateName}
               </div>
            )}
            <div
               className={classNames(
                  disableSpinner ||
                     !scrollRef.current ||
                     scrollRef.current.scrollHeight <= scrollRef.current.offsetHeight
                     ? 'hidden'
                     : 'block',
                  'flex flex-col items-center',
               )}
            >
               <svg
                  role="status"
                  className="inline w-6 h-6 mr-2 text-gray-200 animate-spin dark:text-gray-600 fill-gray-600 dark:fill-gray-300"
                  viewBox="0 0 100 101"
                  fill="none"
               >
                  <path
                     d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z"
                     fill="currentColor"
                  />
                  <path
                     d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z"
                     fill="currentFill"
                  />
               </svg>
               <span className="text-sm text-slate-400 font-extralight">Loading more messages...</span>
            </div>
         </ul>
         {deleted || (
            <form
               className="relative flex items-center px-4 border-t-2 h-14 border-slate-200 gap-x-4"
               action={null}
               onSubmit={sendMessage}
            >
               <Popover
                  opened={openedPicker}
                  onClose={() => setOpenedPicker(false)}
                  target={
                     <button type="button" className="w-7 h-7" onClick={() => setOpenedPicker((c) => !c)}>
                        <MdEmojiEmotions className="w-full h-full text-slate-500" />
                     </button>
                  }
                  position="top"
                  spacing={0}
                  gutter={20}
                  withArrow
               >
                  <EmojiPickerNoSSRWrapper
                     onEmojiClick={(e, emojiObject) => {
                        setMessageContent((c) => {
                           const caretPosition = inputBoxRef.current?.selectionStart || 0
                           const arr = c.split('')
                           return [
                              ...arr.slice(0, caretPosition),
                              emojiObject.emoji,
                              ...arr.slice(caretPosition, c.length),
                           ].join('')
                        })
                     }}
                  />
               </Popover>
               <input
                  className="py-1.5 px-4 bg-gray-200 border-none rounded-full w-full focus:outline-none"
                  placeholder="Aa"
                  value={messageContent}
                  onChange={(e) => setMessageContent(e.target.value)}
                  ref={inputBoxRef}
                  required
               />
               <button type="submit" className="p-2 bg-teal-600 rounded-full">
                  <MdSend className="w-6 h-6 text-white" />
               </button>
               {otherIsTyping && (
                  <div className="absolute flex items-center text-sm text-teal-500 select-none -top-7 gap-x-2">
                     <div className="ticontainer">
                        <div className="tiblock">
                           <div className="bg-pink-500 tidot"></div>
                           <div className="bg-orange-500 tidot"></div>
                           <div className="bg-yellow-500 tidot"></div>
                           <div className="bg-green-500 tidot"></div>
                        </div>
                     </div>
                     {conversation.otherName} is typing...
                  </div>
               )}
            </form>
         )}
      </div>
   )
}

export default ChatBox
