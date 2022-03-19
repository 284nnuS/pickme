import { Popover } from '@mantine/core'
import { useNotifications } from '@mantine/notifications'
import classNames from 'classnames'
import React, { useEffect, useState } from 'react'
import { IoMdNotifications } from 'react-icons/io'
import { io } from 'socket.io-client'
import NotificationForm from './NotificationForm'

function NotificationBox({ yourId, inProfile = false }) {
   const [notificationList, setNotificationList] = useState([])

   const socket = io('/notify', {
      forceNew: true,
      upgrade: false,
      transports: ['websocket'],
   })
   const [init, setInit] = useState(false)

   const process = (notifications: Notification[]) =>
      notifications.filter(
         (v: Notification, i: number, a: Notification[]) =>
            a.findIndex((t) => t.notificationId === v.notificationId) === i && v.targetUID === yourId,
      )

   const notify = useNotifications()

   useEffect(() => {
      socket.open()
      socket
         .on('Notifications', (notifications: Notification[]) => {
            setNotificationList((current: Notification[]) => process([...current, ...notifications]))
         })
         .on('notification:seenAll', () => {
            setNotificationList((current) =>
               current.map((el: Notification) => {
                  return { ...el, seen: true }
               }),
            )
         })
         .on('New notification', (notification: Notification) =>
            setNotificationList((current: Notification[]) => process([...current, notification])),
         )
         .on('Success', (res: { title: string; message: string }) => {
            notify.showNotification({
               ...res,
               color: 'green',
            })
         })
         .on('Error', (res: { title: string; message: string }) => {
            notify.showNotification({
               ...res,
               color: 'red',
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
         socket.emit('notification:get')
      }
   }, [init])

   const [opened, setOpened] = useState(false)

   const [all, setAll] = useState(false)

   const unseen = notificationList.filter((el: Notification) => !el.seen).length

   const filtered = notificationList.filter((el: Notification) => all || !el.seen)

   return (
      <Popover
         opened={opened}
         position="bottom"
         spacing={0}
         radius={10}
         classNames={{
            body: 'border border-teal-500',
         }}
         onClose={() => setOpened(false)}
         target={
            <button
               className={classNames(
                  'rounded-full text-white',
                  inProfile ? 'hover:bg-slate-300/60 w-10 h-10 p-1' : ' p-1.5 bg-teal-500 w-8 h-8',
               )}
               onClick={() => setOpened((c) => !c)}
            >
               <IoMdNotifications className="w-full h-full text-white " />
               {unseen > 0 && (
                  <div className="w-6 h-6 rounded-full bg-[#fe4457] text-white text-center absolute -bottom-2 -right-2">
                     {unseen}
                  </div>
               )}
            </button>
         }
      >
         <div className="w-[28rem] flex flex-col">
            <div className="flex w-full border-b border-teal-500">
               <button
                  className={classNames(
                     'w-20 py-1 font-semibold rounded-tl-[0.4rem] focus:outline-none',
                     all ? 'text-white bg-teal-600' : 'bg-teal-100 text-teal-500 border-teal-500',
                  )}
                  onClick={() => setAll(true)}
               >
                  All
               </button>
               <button
                  className={classNames(
                     'w-20 py-1 font-semibold textfocus:outline-none',
                     all ? 'bg-teal-100 text-teal-500 border-r border-teal-500' : 'text-white bg-teal-600',
                  )}
                  onClick={() => setAll(false)}
               >
                  Unseen
               </button>
            </div>
            {filtered.length > 0 ? (
               filtered.map((n: Notification) => (
                  <NotificationForm key={n.notificationId} notification={n} socket={socket} />
               ))
            ) : (
               <div className="flex items-center justify-center h-20 italic text-gray-500">
                  You don&#39;t have any notifications
               </div>
            )}
            {unseen > 0 && (
               <button
                  className="self-center px-2 mb-2 text-sm border-b-1 text-slate-400 border-b-slate-400 hover:font-semibold"
                  onClick={() => {
                     socket.emit('notification:seenAll')
                     setOpened(false)
                  }}
               >
                  Clear
               </button>
            )}
         </div>
      </Popover>
   )
}

export default NotificationBox
