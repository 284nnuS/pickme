interface UserProfile {
   userId: number
   name: string
   avatar: string
   birthday: number
   gender: string
   bio: string
   avatar: string
   interests: string[]
   images?: File[]
   matches: {
      userId: number
      name: string
      avatar: string
   }[]
   likes: number
   address: string
   phone: string
   statusEmoji: string
   statusText: string
}
