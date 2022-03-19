import { IoMdNotifications } from 'react-icons/io'
import React, { useRef } from 'react'
import { Image } from '@mantine/core'
import Link from 'next/link'
import { getDiffTimeToString } from '~/src/utils/time'
import { Socket } from 'socket.io-client'

const NotificationForm = ({ notification, socket }: { notification: Notification; socket: Socket }) => {
   const linkRef = useRef<HTMLAnchorElement>()

   if (notification.eventType === 'match')
      notification = {
         ...notification,
         message: 'You have a new matched!',
         link: '/app/chat/' + notification.sourceUID,
      }

   if (notification.eventType === 'react')
      notification = {
         ...notification,
         message: 'It’s seem someone liked you!',
         avatar: '/static/images/pickme.png',
      }

   return (
      <button
         type="button"
         onClick={() => {
            notification.link && linkRef.current?.click()
            notification.seen ||
               socket.emit('notification:seen', {
                  userId: notification.targetUID,
                  notificationId: notification.notificationId,
               })
         }}
         className="flex items-center justify-start w-full h-20 px-3 hover:rounded-xl hover:bg-slate-300"
      >
         <div className="relative p-1 bg-white border-2 border-teal-700 rounded-full">
            <Image src={notification.avatar} radius={100} width={50} height={50} alt="" />
            <IoMdNotifications className="absolute bottom-0 right-0 w-5 h-5 p-0.5 text-white bg-blue-600 rounded-full" />
         </div>
         <div className="ml-5 w-[20rem] mr-5 ">
            <div className="text-left ">{notification.message}</div>
            <div className="text-left text-gray-400">{getDiffTimeToString(notification.time)}</div>
         </div>
         {notification.seen || <div className="w-5 h-5 bg-teal-400 rounded-full"></div>}
         <div className="hidden">
            <Link href={notification.link || ''} passHref>
               <a ref={linkRef}>Link</a>
            </Link>
         </div>
      </button>
   )
}
export default NotificationForm
