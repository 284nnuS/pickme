import { IoMdNotifications } from 'react-icons/io'
import { Image } from '@mantine/core'
import { getDiffTimeToString } from '~/src/utils/time'
import { Socket } from 'socket.io-client'
import { useRouter } from 'next/router'

const NotificationForm = ({ notification, socket }: { notification: Notification; socket: Socket }) => {
   const router = useRouter()

   if (notification.eventType === 'match')
      notification = {
         ...notification,
         message: 'You have a new matched!',
      }

   if (notification.eventType === 'react')
      notification = {
         ...notification,
         message: 'It’s seem someone liked you!',
         avatar: '/static/images/pickme.png',
      }

   console.log(notification)

   return (
      <button
         type="button"
         onClick={() => {
            notification.link && router.push(notification.link)
            notification.seen ||
               socket.emit('notification:seen', {
                  userId: notification.targetUID,
                  notificationId: notification.notificationId,
               })
         }}
         className="flex items-center justify-start w-full h-20 px-3 hover:rounded-xl hover:bg-slate-300 gap-x-3"
      >
         <div className="relative p-1 bg-white border-2 border-teal-700 rounded-full">
            <Image src={notification.avatar} radius={100} width={50} height={50} alt="" />
            <IoMdNotifications className="absolute bottom-0 right-0 w-5 h-5 p-0.5 text-white bg-blue-600 rounded-full" />
         </div>
         <div className="flex-grow">
            <div className="text-left ">{notification.message}</div>
            <div className="text-left text-gray-400">{getDiffTimeToString(notification.time)}</div>
         </div>
         {notification.seen || <div className="w-5 h-5 bg-teal-400 rounded-full"></div>}
      </button>
   )
}
export default NotificationForm
