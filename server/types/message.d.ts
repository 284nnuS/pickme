interface Message {
   messageId: number
   conversationId: number
   time: number
   content?: string
   react?: React
   sender: number
}

interface Conversation {
   conversationId: number
   otherId: number
   otherName: string
   otherAvatar: string
   sender: boolean
   latestTime: number
   latestMessage: string
}
