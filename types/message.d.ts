interface Message {
   messageId: number
   time: number
   sender: number
   receiver: number
   content: string
   react?: string
}

interface GetMoreMessages {
   time: number
   requestUID: number
   targetUID: number
   num: number
}
