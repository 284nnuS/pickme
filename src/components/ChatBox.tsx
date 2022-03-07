import { useState, useEffect, useRef, useCallback, FormEvent } from 'react'
import classNames from 'classnames'
import { MdSend } from 'react-icons/md'
import { io } from 'socket.io-client'
import { useThrottle, useThrottleCallback } from '@react-hook/throttle'
import { ReactSelect } from '.'
import { Image, Tooltip } from '@mantine/core'
import { AiOutlineRollback } from 'react-icons/ai'

function ChatBox({ yourId, other }: { yourId: number; other: UserInfo }) {
   const socket = io()
   const [init, setInit] = useState(false)
   const [messageList, setMessageList] = useState<Message[]>([])
   const [canLoadMore, setCanLoadMore] = useThrottle(false, 1, true)
   const listRef = useRef<HTMLUListElement>()

   const [disableSpinner, setDisableSpinner] = useState(false)

   const process = (messages: Message[]) =>
      messages
         .filter((v: Message, i: number, a: Message[]) => a.findIndex((t) => t.messageId === v.messageId) === i)
         .filter(
            (el: Message) =>
               (el.sender === yourId && el.receiver === other.userId) ||
               (el.sender === other.userId && el.receiver === yourId),
         )
         .sort((a: Message, b: Message) => b.time - a.time)

   useEffect(() => {
      socket.open()
      socket
         .on('Messages', (messages: Message[]) => {
            if (messages && messages.length > 0) {
               setCanLoadMore(true)
               setMessageList((currentMessages) => process([...currentMessages, ...messages]))
            } else setDisableSpinner(true)
         })
         .on('New message', (newMessage: Message) =>
            setMessageList((currentMessages) => process([newMessage, ...currentMessages])),
         )
         .on('React to message', ({ messageId, sender, receiver, react }: ReactToMessage) =>
            setMessageList((current) => {
               const index = current.findIndex((t) => t.messageId === messageId)
               if (
                  (current[index].sender === sender && current[index].receiver === receiver) ||
                  (current[index].sender === receiver && current[index].receiver === sender)
               ) {
                  current[index].react = react
               }
               return [...current]
            }),
         )
         .on('Delete message', ({ messageId, sender, receiver }: DeleteMessage) =>
            setMessageList((current) => {
               const index = current.findIndex((t) => t.messageId === messageId)
               if (
                  (current[index].sender === sender && current[index].receiver === receiver) ||
                  (current[index].sender === receiver && current[index].receiver === sender)
               ) {
                  current[index].content = null
               }
               return [...current]
            }),
         )
         .on('disconnect', () => {
            socket.removeAllListeners()
         })

      setInit(true)
   }, [])

   useEffect(() => {
      if (init) {
         const now = new Date()
         const currentTime = now.getTime() - now.getTimezoneOffset() * 60000
         const req: GetMoreMessages = {
            time: currentTime,
            otherId: other.userId,
            num: 15,
         }
         socket.emit('Get more messages', req)
         listRef.current.scrollTop = listRef.current.scrollHeight
      }
   }, [init])

   const handleScroll = useCallback(
      (e) => {
         const getMoreMessages = (time: number, num: number) => {
            const req: GetMoreMessages = { time, otherId: other.userId, num }
            socket.emit('Get more messages', req)
         }
         const list = e.target
         if (canLoadMore && list.scrollTop <= list.lastChild.offsetTop) {
            getMoreMessages(messageList[messageList.length - 1].time, 15)
            setCanLoadMore(false)
         }
      },
      [other.userId, socket, canLoadMore, messageList, setCanLoadMore],
   )

   const throttledhandleScroll = useThrottleCallback(handleScroll, 0.5, true)

   const inputBoxRef = useRef<HTMLInputElement>()

   const sendMessage = (e: FormEvent) => {
      const inputBox = inputBoxRef.current
      const req: SendMessage = { otherId: other.userId, content: inputBox.value }
      socket.emit('Send message', req)
      inputBox.value = ''
      e.preventDefault()
   }

   useEffect(() => {
      if (init) {
         const list = listRef.current
         list.addEventListener('scroll', throttledhandleScroll)
         return () => {
            list.removeEventListener('scroll', throttledhandleScroll)
         }
      }
   }, [init, throttledhandleScroll])

   const abbreviate = (fullName: string) => {
      const words = fullName.split(' ')
      const short = words[0] + (words.length > 1 ? ' ' + words[words.length - 1] : '')
      return short
         .replace(/\b(\w)\w+/g, '$1.')
         .replace(/\s/g, '')
         .replace(/\.$/, '')
         .toUpperCase()
   }

   const abbreviateName = abbreviate(other.name)

   return (
      <div className="w-screen h-screen overflow-hidden">
         <div className="flex items-center h-16 px-6 py-2 border-b-2 gap-x-5 border-slate-200">
            {other.avatar ? (
               <Image src={other.avatar} radius={100} alt="Avatar" width={48} height={48} />
            ) : (
               <p className="w-12 h-12 text-lg font-bold text-center text-white bg-teal-600 rounded-full leading-[3rem]">
                  {abbreviateName}
               </p>
            )}
            <p className="text-lg font-semibold">{other.name}</p>
            <div className="flex-grow"></div>
            <button className="p-2 text-sm bg-white border-2 rounded-full border-slate-400 hover:bg-slate-400 hover:text-white">
               UNMATCH
            </button>
         </div>
         <ul className="flex flex-col-reverse p-6 overflow-y-auto h-[calc(100%-7.5rem)]" ref={listRef}>
            {Object.values(messageList).map((el, i, list) => {
               const youIsSender = yourId === el.sender
               const currentTime = new Date()
               const nextMessageTime = i < list.length - 1 && new Date(list[i + 1].time)
               const currentMessageTime = new Date(el.time)
               const nextMessageIsMoreThanADay =
                  nextMessageTime && (currentTime.getTime() - nextMessageTime.getTime()) / (1000 * 60 * 60 * 24) > 1.0
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
                           (other.avatar ? (
                              <Image
                                 src={other.avatar}
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
                                 (currentMessageIsMoreThanADay ? currentMessageTime.toLocaleDateString() + ' ' : '') +
                                 currentMessageTime.toLocaleTimeString()
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
                                 {el.content && el.react && (
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
                                          const req: DeleteMessage = {
                                             messageId: el.messageId,
                                             sender: el.sender,
                                             receiver: el.receiver,
                                          }
                                          socket.emit('Delete message', req)
                                       }}
                                    >
                                       <AiOutlineRollback className="w-full h-full text-gray-500" />
                                    </button>
                                 </div>
                              ) : (
                                 <ReactSelect socket={socket} message={el} />
                              ))}
                        </div>
                     </div>
                  </li>
               )
            })}
            <div
               className={classNames(
                  disableSpinner || !listRef.current || listRef.current.scrollHeight <= listRef.current.offsetHeight
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
         <form className="flex items-center px-4 border-t-2 h-14 border-slate-200" action={null} onSubmit={sendMessage}>
            <input
               className="py-1.5 px-4 bg-gray-200 border-none rounded-full w-full focus:outline-none"
               placeholder="Aa"
               ref={inputBoxRef}
               required
            />
            <button type="submit" className="p-2 ml-3 bg-teal-500 rounded-full">
               <MdSend className="w-6 h-6 text-white" />
            </button>
         </form>
      </div>
   )
}

export default ChatBox
