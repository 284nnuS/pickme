interface Notification {
   notificationId: number
   sourceUID: number
   targetUID: number
   time: number
   avatar: string
   eventType: 'match' | 'warn' | 'react'
   seen: boolean
   message: string
   link: string
}
