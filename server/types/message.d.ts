interface Message {
   messageId: number
   time: number
   sender: number
   receiver: number
   content?: string
   react?: React
}

interface GetMoreMessages {
   time: number
   otherId: number
   num: number
}

interface SendMessage {
   otherId: number
   content: string
}

interface ReactToMessage {
   messageId: number
   sender: number
   receiver: number
   content?: string
   react?: React
}

interface DeleteMessage {
   messageId: number
   sender: number
   receiver: enumber
}
