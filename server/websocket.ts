import { Server, Socket } from 'socket.io'
import http from 'http'
import { NextApiRequest } from 'next'
import { getToken } from 'next-auth/jwt'
import { parseCookies } from '../shared/utils'
import { getUserInfo } from '../shared/nextAuthOptions'
import env from '../shared/env'
import axios from 'axios'

const sio = new Server()

sio.use(async (socket, next) => {
   const token = await getToken({
      req: {
         headers: socket.request.headers,
         cookies: parseCookies(socket.request),
      } as NextApiRequest,
      secret: process.env.SECRET_KEY,
   })
   const res = await getUserInfo(token.email)
   if (res && 'data' in res) {
      const userId = res['data'].userId
      socket['userId'] = userId
      next()
   }
})

sio.on('connection', (socket: Socket) => {
   socket.join('uid-' + socket['userId'])

   socket
      .on('Get more messages', ({ time, otherId, num }: GetMoreMessages) => {
         const getMessageReq = {
            time,
            userIdOne: socket['userId'],
            userIdTwo: otherId,
            num,
         }

         axios.post(`${env.javaServerUrl}/message/get`, getMessageReq).then((res) => {
            const messages: Message[] = res.data.data
            sio.emit('Messages', messages)
         })
      })
      .on('Send message', ({ otherId, content }: SendMessage) => {
         const message = {
            time: new Date().getTime(),
            sender: socket['userId'],
            receiver: otherId,
            content,
         }

         axios
            .post(`${env.javaServerUrl}/message`, message)
            .then((res) => {
               const message: Message = res.data.data
               sio.emit('New message', message)
               sio.to('uid-' + otherId).emit('New message', message)
            })
            .catch((err) => {
               console.log(err.response.data)
            })
      })
      .on('React to message', ({ messageId, sender, receiver, content, react }: ReactToMessage) => {
         if (sender !== socket['userId'] && receiver !== socket['userId']) return
         console.log({ messageId, sender, receiver, content, react })
         axios.put(`${env.javaServerUrl}/message`, { messageId, sender, receiver, content, react }).then((response) => {
            const message: Message = response.data.data
            sio.emit('React to message', message)
            sio.to('' + (message.sender === (socket['userId'] as number) ? message.receiver : message.sender)).emit(
               'React to message',
               message,
            )
         })
      })
      .on('Delete message', ({ messageId, sender, receiver }: DeleteMessage) => {
         if (sender !== socket['userId'] && receiver !== socket['userId']) return
         axios.put(`${env.javaServerUrl}/message`, { messageId, sender, receiver, content: null }).then((response) => {
            const message: Message = response.data.data
            sio.emit('Delete message', message)
            sio.to('' + (message.sender === (socket['userId'] as number) ? message.receiver : message.sender)).emit(
               'Delete message',
               message,
            )
         })
      })
      .on('disconnect', () => {
         socket.removeAllListeners()
      })
})

export default function attach(server: http.Server) {
   sio.attach(server)
}
