import { Server, Socket } from 'socket.io'
import http from 'http'
import { NextApiRequest } from 'next'
import { getToken } from 'next-auth/jwt'
import { parseCookies } from '../shared/utils'
import { getUserInfo } from '../shared/nextAuthOptions'
import env from '../shared/env'
import axios from 'axios'

const sio = new Server()

const authMiddleware = async (socket: Socket, next: () => void) => {
   try {
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
   } catch {
      //
   }
}

sio.of('/match')
   .use(authMiddleware)
   .on('connection', (socket: Socket) => {
      socket.join('match-' + socket['userId'])

      socket
         .on('Get matched users', async () => {
            try {
               let matchedUsers: UserProfile[] = (
                  await axios.get(`${env.javaServerUrl}/profile/matched/id/${socket['userId']}`)
               ).data['data']

               matchedUsers = await Promise.all(
                  matchedUsers.map(async (el) => {
                     el.images = (await axios.get(`${env.javaServerUrl}/file/${el.userId}/photo`)).data['data']
                     return el
                  }),
               )

               socket.to('match-' + socket['userId']).emit('Match list', matchedUsers)
            } catch (err) {
               //
            }
         })
         .on('Reroll', async () => {
            let cards: Card[] = (await axios.get(`${env.javaServerUrl}/profile/unmatched/id/${socket['userId']}`)).data[
               'data'
            ]

            cards = await Promise.all(
               cards.map(async (el) => {
                  el.images = (await axios.get(`${env.javaServerUrl}/file/${el.userId}/photo`)).data['data']
                  return el
               }),
            )

            socket.to('match-' + socket['userId']).emit('Reroll', cards)
         })
         .on('Get cards', async () => {
            try {
               let cards: Card[] = (await axios.get(`${env.javaServerUrl}/profile/unmatched/id/${socket['userId']}`))
                  .data['data']

               cards = await Promise.all(
                  cards.map(async (el) => {
                     el.images = (await axios.get(`${env.javaServerUrl}/file/${el.userId}/photo`)).data['data']
                     return el
                  }),
               )

               socket.to('match-' + socket['userId']).emit('Cards', cards)
            } catch {
               //
            }
         })
         .on('Swipe', async ({ id, like }: { id: number; like: boolean }) => {
            try {
               await axios.post(`${env.javaServerUrl}/matchStatus`, {
                  userIdOne: socket['userId'],
                  userIdTwo: id,
                  like,
               })
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
                           .to('notify-' + id)
                           .emit('New notification', notification)

                        let profile: UserProfile = (
                           await axios.get(`${env.javaServerUrl}/profile/id/${socket['userId']}`)
                        ).data['data']
                        profile.images = (await axios.get(`${env.javaServerUrl}/file/${socket['userId']}/photo`)).data[
                           'data'
                        ]

                        socket.to('match-' + id).emit('New match', profile)

                        // Send matched notification to user two
                        res = await axios.post(`${env.javaServerUrl}/notify`, {
                           ...req,
                           sourceUID: id,
                           targetUID: socket['userId'],
                        })

                        notification = res.data['data']
                        sio.of('/notify')
                           .to('notify-' + socket['userId'])
                           .emit('New notification', notification)

                        profile = (await axios.get(`${env.javaServerUrl}/profile/id/${id}`)).data['data']
                        profile.images = (await axios.get(`${env.javaServerUrl}/file/${id}/photo`)).data['data']

                        socket.to('match-' + socket['userId']).emit('New match', profile)
                     }
                  } catch (err) {
                     console.log(err)
                  }
               }
            } catch (err) {
               console.log(err)
            }
         })
         .on('React', async ({ id, name }: { id: number; name: string }) => {
            try {
               const res = await axios.post(`${env.javaServerUrl}/notify`, {
                  time: new Date().getTime(),
                  sourceUID: socket['userId'],
                  targetUID: id,
                  eventType: 'react',
                  seen: false,
               })
               const notification = res.data['data']

               sio.of('/notify')
                  .to('notify-' + socket['userId'])
                  .emit('Success', {
                     title: 'Super Like',
                     message: '🎉 You sent a super like to ' + name.split(' ')[0] + ' 🎉',
                  })

               sio.of('/notify')
                  .to('notify-' + id)
                  .emit('New notification', notification)
            } catch {
               sio.of('/notify')
                  .to('notify-' + socket['userId'])
                  .emit('Error', {
                     title: 'Super Like 👍',
                     message: 'You already sent a super like to ' + name.split(' ')[0] + ' ❌',
                  })
            }
         })
         .on('UnMatch', async ({ id, name }: { id: number; name: string }) => {
            try {
               await axios.put(`${env.javaServerUrl}/matchStatus`, {
                  userIdOne: socket['userId'],
                  userIdTwo: id,
                  like: false,
               })

               sio.of('/notify')
                  .to('notify-' + socket['userId'])
                  .emit('Success', {
                     title: 'Unmatch',
                     message: 'You unmatched with ' + name.split(' ')[0] + ' ☹️',
                  })

               socket.to('match-' + id).emit('Remove match list', socket['userId'])
            } catch {
               sio.of('/notify')
                  .to('notify-' + socket['userId'])
                  .emit('Error', {
                     title: 'Unmatch',
                     message: 'Error occurred while unmatching ❌',
                  })
            }
         })
         .on('disconnect', () => {
            socket.removeAllListeners()
         })
   })

sio.of('/notify')
   .use(authMiddleware)
   .on('connection', (socket: Socket) => {
      socket.join('notify-' + socket['userId'])

      socket
         .on('Get notifications', async () => {
            const res = await axios.get(`${env.javaServerUrl}/notify/userId/${socket['userId']}`)
            const notifications = res.data.data
            socket.to('notify-' + socket['userId']).emit(
               'Notifications',
               notifications.map((el: Notification) => {
                  if (el.eventType !== 'react') return el
                  delete el.sourceUID
                  return el
               }),
            )
         })
         .on('Seen', async ({ userId, notificationId }: { userId: number; notificationId: number }) => {
            if (userId !== socket['userId']) return
            await axios.get(`${env.javaServerUrl}/notify/seen/${userId}/${notificationId}`)
         })
         .on('Seen all', async () => {
            await axios.get(`${env.javaServerUrl}/notify/seenAll/userId/${socket['userId']}`)
            socket.to('notify-' + socket['userId']).emit('Seen all')
         })
         .on('disconnect', () => {
            socket.removeAllListeners()
         })
   })

sio.of('/chat')
   .use(authMiddleware)
   .use(async (socket: Socket, next: () => void) => {
      try {
         const conversationId = socket.handshake.query['conversationId'] as string

         if (!conversationId) return

         const conversation = (
            await axios.get(`${env.javaServerUrl}/conversation/check/${conversationId}/${socket['userId']}`)
         ).data['data']
         socket['otherId'] = conversation['otherId'] as string

         next()
      } catch (err) {
         //
      }
   })
   .on('connection', (socket: Socket) => {
      socket.join('chat-' + socket['userId'])

      socket
         .on('Get conversations', async () => {
            if (!socket.handshake.query['conversationId']) return
            try {
               const conversations = (await axios.get(`${env.javaServerUrl}/conversation/${socket['userId']}`)).data[
                  'data'
               ]
               socket.to('chat-' + socket['userId']).emit('Conversations', conversations)
            } catch {
               //
            }
         })
         .on('Get more messages', async (time: number) => {
            if (!socket.handshake.query['conversationId']) return

            try {
               const messages = (
                  await axios.get(`${env.javaServerUrl}/message/${socket.handshake.query['conversationId']}/${time}`)
               ).data['data']

               socket.to('chat-' + socket['userId']).emit('Messages', messages)
            } catch {
               //
            }
         })
         .on('Send message', async (content: string) => {
            if (!socket.handshake.query['conversationId']) return

            try {
               const message = (
                  await axios.post(`${env.javaServerUrl}/message`, {
                     conversationId: socket.handshake.query['conversationId'],
                     time: new Date().getTime(),
                     content,
                     sender: socket['userId'],
                  })
               ).data['data']

               message['react'] = 'none'

               socket
                  .to('chat-' + socket['userId'])
                  .to('chat-' + socket['otherId'])
                  .emit('New message', message)
            } catch {
               //
            }
         })
         .on('React to message', async ({ messageId, react }: { messageId: number; react: string }) => {
            if (!socket.handshake.query['conversationId']) return

            try {
               const message = (
                  await axios.put(`${env.javaServerUrl}/message`, {
                     messageId,
                     conversationId: socket.handshake.query['conversationId'] as string,
                     react,
                  })
               ).data['data']

               socket
                  .to('chat-' + socket['userId'])
                  .to('chat-' + socket['otherId'])
                  .emit('React to message', message)
            } catch {
               //
            }
         })
         .on('Delete message', async (messageId: string) => {
            if (!socket.handshake.query['conversationId']) return

            const message = (
               await axios.put(`${env.javaServerUrl}/message`, {
                  messageId,
                  conversationId: socket.handshake.query['conversationId'] as string,
               })
            ).data['data']

            socket
               .to('chat-' + socket['userId'])
               .to('chat-' + socket['otherId'])
               .emit('Delete message', message)
         })
         .on('disconnect', () => {
            socket.removeAllListeners()
         })
   })

export default function attach(server: http.Server) {
   sio.attach(server)
}
