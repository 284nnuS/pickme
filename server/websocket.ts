import { Server, Socket } from 'socket.io'
import http from 'http'
import { NextApiRequest } from 'next'
import { getToken } from 'next-auth/jwt'
import { parseCookies } from '../shared/utils'
import { getUserInfo } from '../shared/nextAuthOptions'
import env from '../shared/env'
import axios from 'axios'

const sio = new Server()

const authMiddleware = async (socket, next) => {
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
}

sio.of('/match')
   .use(authMiddleware)
   .on('connection', (socket: Socket) => {
      socket.join('uid-' + socket['userId'])

      socket
         .on('Get match list', () => {
            axios.get(`${env.javaServerUrl}/user/matched/id/${socket['userId']}`).then((res) => {
               const matchedUsers = res.data.data
               socket.to('uid-' + socket['userId']).emit('Match list', matchedUsers)
            })
         })
         .on('Reroll', () => {
            axios.get(`${env.javaServerUrl}/user/card/id/${socket['userId']}`).then((res) => {
               const cards = res.data.data
               socket.to('uid-' + socket['userId']).emit('Reroll', cards)
            })
         })
         .on('Get cards', () => {
            axios.get(`${env.javaServerUrl}/user/card/id/${socket['userId']}`).then((res) => {
               const cards = res.data.data
               socket.to('uid-' + socket['userId']).emit('Cards', cards)
            })
         })
         .on('Swipe', ({ id, like }: { id: number; like: boolean }) => {
            axios
               .post(`${env.javaServerUrl}/matchStatus`, {
                  userIdOne: socket['userId'],
                  userIdTwo: id,
                  like,
               })
               .then(async () => {
                  if (like) {
                     try {
                        let res = await axios.get(`${env.javaServerUrl}/matchStatus/${socket['userId']}/${id}`)
                        const match: boolean = res.data['data']
                        if (match) {
                           const req = {
                              time: new Date().getTime(),
                              eventType: 'match',
                              seen: false,
                           }

                           // Send matched notification to user one
                           res = await axios.post(`${env.javaServerUrl}/notify`, {
                              ...req,
                              sourceUID: socket['userId'],
                              targetUID: id,
                           })
                           let notification = res.data['data']

                           sio.of('/notify')
                              .to('uid-' + id)
                              .emit('New notification', notification)

                           res = await axios.get(`${env.javaServerUrl}/user/basic/id/${socket['userId']}`)
                           socket.to('uid-' + id).emit('New match', res.data['data'])

                           // Send matched notification to user two
                           res = await axios.post(`${env.javaServerUrl}/notify`, {
                              ...req,
                              sourceUID: id,
                              targetUID: socket['userId'],
                           })

                           notification = res.data['data']
                           sio.of('/notify')
                              .to('uid-' + socket['userId'])
                              .emit('New notification', notification)

                           res = await axios.get(`${env.javaServerUrl}/user/basic/id/${id}`)
                           socket.to('uid-' + socket['userId']).emit('New match', res.data['data'])
                        }
                     } catch (e) {
                        console.log(e)
                     }
                  }
               })
         })
         .on('React', ({ id, name }: { id: number; name: string }) => {
            axios
               .post(`${env.javaServerUrl}/notify`, {
                  time: new Date().getTime(),
                  sourceUID: socket['userId'],
                  targetUID: id,
                  eventType: 'react',
                  seen: false,
               })
               .then((res) => {
                  const notification = res.data['data']

                  sio.of('/notify')
                     .to('uid-' + socket['userId'])
                     .emit('Success', {
                        title: 'Super Like',
                        message: '🎉 You sent a super like to ' + name.split(' ')[0] + ' 🎉',
                     })

                  sio.of('/notify')
                     .to('uid-' + id)
                     .emit('New notification', notification)
               })
               .catch(() => {
                  sio.of('/notify')
                     .to('uid-' + socket['userId'])
                     .emit('Error', {
                        title: 'Super Like 👍',
                        message: 'You already sent a super like to ' + name.split(' ')[0] + ' ❌',
                     })
               })
         })
         .on('Unmatch', ({ id, name }: { id: number; name: string }) => {
            axios
               .put(`${env.javaServerUrl}/matchStatus`, {
                  userIdOne: socket['userId'],
                  userIdTwo: id,
                  like: false,
               })
               .then(() => {
                  socket.to('uid-' + socket['userId']).emit('Success', {
                     title: 'Unmatch',
                     message: 'You unmatched with ' + name.split(' ')[0] + ' ☹️',
                  })

                  socket.to('uid-' + socket['userId']).emit('Remove in match list', id)
                  socket.to('uid-' + id).emit('Remove in match list', socket['userId'])
               })
         })
         .on('disconnect', () => {
            socket.removeAllListeners()
         })
   })

sio.of('/notify')
   .use(authMiddleware)
   .on('connection', (socket: Socket) => {
      socket.join('uid-' + socket['userId'])

      socket
         .on('Get notifications', () => {
            axios.get(`${env.javaServerUrl}/notify/userId/${socket['userId']}`).then((res) => {
               const notifications = res.data.data
               socket.to('uid-' + socket['userId']).emit(
                  'Notifications',
                  notifications.map((el: Notification) => {
                     if (el.eventType !== 'react') return el
                     delete el.sourceUID
                     return el
                  }),
               )
            })
         })
         .on('Seen', ({ userId, notificationId }: { userId: number; notificationId: number }) => {
            if (userId !== socket['userId']) return
            axios.get(`${env.javaServerUrl}/notify/seen/${userId}/${notificationId}`).then(() => {
               //
            })
         })
         .on('Seen all', () => {
            axios.get(`${env.javaServerUrl}/notify/seenAll/userId/${socket['userId']}`).then(() => {
               socket.to('uid-' + socket['userId']).emit('Seen all')
            })
         })
         .on('disconnect', () => {
            socket.removeAllListeners()
         })
   })

sio.of('/chat')
   .use(authMiddleware)
   .on('connection', (socket: Socket) => {
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
               socket.to('uid-' + socket['userId']).emit('Messages', messages)
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
                  socket.to('uid-' + socket['userId']).emit('New message', message)
                  socket.to('uid-' + otherId).emit('New message', message)
               })
               .catch((err) => {
                  console.log(err.response.data)
               })
         })
         .on('React to message', ({ messageId, sender, receiver, content, react }: ReactToMessage) => {
            if (sender !== socket['userId'] && receiver !== socket['userId']) return
            axios
               .put(`${env.javaServerUrl}/message`, { messageId, sender, receiver, content, react })
               .then((response) => {
                  const message: Message = response.data.data
                  socket.to('uid-' + socket['userId']).emit('React to message', message)
                  socket
                     .to('uid-' + (message.sender === (socket['userId'] as number) ? message.receiver : message.sender))
                     .emit('React to message', message)
               })
         })
         .on('Delete message', ({ messageId, sender, receiver }: DeleteMessage) => {
            if (sender !== socket['userId'] && receiver !== socket['userId']) return
            axios
               .put(`${env.javaServerUrl}/message`, { messageId, sender, receiver, content: null })
               .then((response) => {
                  const message: Message = response.data.data
                  socket.to('uid-' + socket['userId']).emit('Delete message', message)
                  socket
                     .to('uid-' + (message.sender === (socket['userId'] as number) ? message.receiver : message.sender))
                     .emit('Delete message', message)
               })
         })
         .on('disconnect', () => {
            socket.removeAllListeners()
         })
   })

export default function attach(server: http.Server) {
   sio.attach(server)
}
