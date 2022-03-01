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
      .on('Get more messages', ({ time, otherId, num }) => {
         const getMessageReq = {
            time,
            userIdOne: socket['userId'],
            userIdTwo: otherId,
            num,
         }

         axios.post(`${env.javaServerUrl}/message/get`, getMessageReq).then((res) => {
            const messages = res.data.data
            sio.emit('Messages', messages)
         })
      })
      .on('Send message', ({ targetId, content }) => {
         const message = {
            time: new Date().getTime(),
            sender: socket['userId'],
            receiver: targetId,
            content,
         }

         axios
            .post(`${env.javaServerUrl}/message`, message)
            .then((res) => {
               const message = res.data.data
               sio.emit('New message', message)
               sio.to('uid-' + targetId).emit('New message', message)
            })
            .catch((err) => {
               console.log(err.response.data)
            })
      })
      .on('React to message', ({ messageId, sender, receiver, react }) => {
         axios.put(`${env.javaServerUrl}/message`, { messageId, sender, receiver, react }).then((response) => {
            const message = response.data.data
            sio.emit('React to message', message)
            sio.to(message.sender === socket['userId'] ? message.receiver : message.sender).emit(
               'React to message',
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
