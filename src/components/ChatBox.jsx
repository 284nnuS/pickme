import { useState, useEffect, useRef, useCallback } from 'react'
import classNames from 'classnames'
import Image from 'next/image'
import { MdSend } from 'react-icons/md'
import { io } from 'socket.io-client'
import { useThrottle, useThrottleCallback } from '@react-hook/throttle'
import { RiEmotionLine } from 'react-icons/ri'
import { Menu, Popover } from '@mantine/core'
import { ReactSelect } from '.'

function ChatBox({ yourId, otherId, otherAvatar, otherName }) {
   const socket = io()
   const [init, setInit] = useState(false)
   const [messageList, setMessageList] = useState([])
   const [canLoadMore, setCanLoadMore] = useThrottle(false, 1, true)
   const listRef = useRef()

   const [disableSpinner, setDisableSpinner] = useState(false)

   const process = (messages) =>
      messages
         .filter((v, i, a) => a.findIndex((t) => t.messageId === v.messageId) === i)
         .filter(
            (el) => (el.sender == yourId && el.receiver == otherId) || (el.sender == otherId && el.receiver == yourId),
         )
         .sort((a, b) => b.time - a.time)

   useEffect(() => {
      socket.open()
      socket
         .on('Messages', (messages) => {
            if (messages && messages.length > 0) {
               setCanLoadMore(true)
               setMessageList((currentMessages) => process([...currentMessages, ...messages]))
            } else setDisableSpinner(true)
         })
         .on('New message', (newMessage) =>
            setMessageList((currentMessages) => process([newMessage, ...currentMessages])),
         )
         .on('React to message', ({ messageId, sender, receiver, react }) =>
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
         .on('disconnect', () => {
            socket.removeAllListeners()
         })

      setInit(true)
   }, [])

   useEffect(() => {
      if (init) {
         const now = new Date()
         const currentTime = now.getTime() - now.getTimezoneOffset() * 60000
         socket.emit('Get more messages', {
            time: currentTime,
            otherId,
            num: 15,
         })
         listRef.current.scrollTop = listRef.current.scrollHeight
      }
   }, [init])

   const handleScroll = useCallback(
      (e) => {
         const getMoreMessages = (time, num) => socket.emit('Get more messages', { time, otherId, num })

         const list = e.target
         if (canLoadMore && list.scrollTop <= list.lastChild.offsetTop) {
            getMoreMessages(messageList[messageList.length - 1].time, 15)
            setCanLoadMore(false)
         }
      },
      [otherId, socket, canLoadMore, messageList, setCanLoadMore],
   )

   const throttledhandleScroll = useThrottleCallback(handleScroll, 0.5, true)

   const inputBoxRef = useRef()

   const sendMessage = (e) => {
      const inputBox = inputBoxRef.current
      socket.emit('Send message', { targetId: otherId, content: inputBox.value })
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

   const abbreviate = (fullName) => {
      const words = fullName.split(' ')
      const short = words[0] + (words.length > 1 ? ' ' + words[words.length - 1] : '')
      return short
         .replace(/\b(\w)\w+/g, '$1.')
         .replace(/\s/g, '')
         .replace(/\.$/, '')
         .toUpperCase()
   }

   const abbreviateName = abbreviate(otherName)

   return (
      <div className="w-screen h-screen overflow-hidden">
         <div className="flex items-center h-16 px-6 py-2 border-b-2 gap-x-5 border-slate-200">
            {otherAvatar ? (
               <Image src={otherAvatar} alt="Avatar" width="48" height="48" />
            ) : (
               <p className="w-12 h-12 text-lg font-bold text-center text-white bg-teal-600 rounded-full leading-[3rem]">
                  {abbreviateName}
               </p>
            )}
            <p className="text-lg font-semibold">{otherName}</p>
            <div className="flex-grow"></div>
            <button className="p-2 text-sm bg-white border-2 rounded-full border-slate-400 hover:bg-slate-400 hover:text-white">
               UNMATCH
            </button>
         </div>
         <ul className="flex flex-col-reverse p-6 overflow-y-auto h-[calc(100%-7.5rem)]" ref={listRef}>
            {Object.values(messageList).map((el, i, list) => {
               const youIsSender = yourId === el.sender
               return (
                  <li key={el.messageId}>
                     <div className={classNames('flex relative', youIsSender && 'flex-row-reverse')}>
                        {youIsSender ||
                           (otherAvatar ? (
                              <Image
                                 src={otherAvatar}
                                 alt="Avatar"
                                 className={
                                    i > 0 && list[i - 1].sender !== el.sender
                                       ? 'absolute left-0 bottom-0 top-auto'
                                       : 'hidden'
                                 }
                                 width="40"
                                 height="40"
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
                              'inline-block max-w-[60%] ml-14 relative group',
                              youIsSender ? 'pl-10' : 'pr-10',
                              i < list.length - 1 && list[i + 1].sender !== el.sender ? 'mt-12' : 'mt-5',
                           )}
                        >
                           <div
                              className={classNames(
                                 youIsSender
                                    ? 'bg-cyan-600 text-white rounded-br-none'
                                    : 'bg-slate-300 text-black rounded-bl-none',
                                 'rounded-3xl p-3 block relative',
                              )}
                           >
                              {el.content}
                              {el.react && (
                                 <div className="absolute flex items-center justify-center w-6 h-6 bg-white border rounded-full -right-2 -bottom-2 z-1">
                                    <Image
                                       src={`/static/images/${el.react}16.png`}
                                       layout="fixed"
                                       width="16"
                                       height="16"
                                       alt={el.react}
                                    />
                                 </div>
                              )}
                           </div>
                           {youIsSender || <ReactSelect socket={socket} message={el} />}
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
