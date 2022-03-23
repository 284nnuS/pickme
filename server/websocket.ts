import { Server, Socket } from 'socket.io'
import http from 'http'
import { NextApiRequest } from 'next'
import { getToken } from 'next-auth/jwt'
import { parseCookies } from '../shared/utils'
import { getUserInfo } from '../shared/nextAuthOptions'
import env from '../shared/env'
import { setupCache } from 'axios-cache-adapter'
import axios from 'axios'
import { cache as userCache } from '../shared/nextAuthOptions'

const cache = setupCache({
   maxAge: 60 * 1000,
})

const api = axios.create({
   adapter: cache.adapter,
})

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
         const user = res['data']

         socket['userId'] = user.userId
         socket['email'] = user.email
         socket['role'] = user.role
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
         .on('matched:get', async () => {
            try {
               let matchedUsers: UserProfile[] = (
                  await axios.get(`${env.javaServerUrl}/profile/matched/id/${socket['userId']}`)
               ).data['data']

               matchedUsers = await Promise.all(
                  matchedUsers.map(async (el) => {
                     el.photos = (await axios.get(`${env.javaServerUrl}/file/${el.userId}/photo`)).data['data']
                     return el
                  }),
               )

               socket.to('match-' + socket['userId']).emit('match:list', matchedUsers)
            } catch (err) {
               //
            }
         })
         .on('reroll', async () => {
            let cards: Card[] = (await axios.get(`${env.javaServerUrl}/profile/unmatched/id/${socket['userId']}`)).data[
               'data'
            ]

            cards = await Promise.all(
               cards.map(async (el) => {
                  el.photos = (await axios.get(`${env.javaServerUrl}/file/${el.userId}/photo`)).data['data']
                  el.voices = (await axios.get(`${env.javaServerUrl}/file/${el.userId}/voice`)).data['data']
                  return el
               }),
            )

            socket.to('match-' + socket['userId']).emit('reroll', cards)
         })
         .on('card:get', async () => {
            try {
               let cards: Card[] = (await axios.get(`${env.javaServerUrl}/profile/unmatched/id/${socket['userId']}`))
                  .data['data']

               cards = await Promise.all(
                  cards.map(async (el) => {
                     el.photos = (await axios.get(`${env.javaServerUrl}/file/${el.userId}/photo`)).data['data']
                     el.voices = (await axios.get(`${env.javaServerUrl}/file/${el.userId}/voice`)).data['data']
                     return el
                  }),
               )

               socket.to('match-' + socket['userId']).emit('cards', cards)
            } catch {
               //
            }
         })
         .on('card:swipe', async ({ id, like }: { id: number; like: boolean }) => {
            try {
               await axios.post(`${env.javaServerUrl}/matchStatus`, {
                  userIdOne: socket['userId'],
                  userIdTwo: id,
                  like,
               })
               if (like) {
                  try {
                     let res = await axios.get(`${env.javaServerUrl}/conversation/${socket['userId']}/${id}`)
                     const conversationId: number = res.data['data']['conversationId']

                     try {
                        const req = {
                           time: new Date().getTime(),
                           eventType: 'match',
                           seen: false,
                           link: '/app/chat/' + conversationId,
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
                           .emit('notification:new', notification)

                        let profile: UserProfile = (
                           await axios.get(`${env.javaServerUrl}/profile/id/${socket['userId']}`)
                        ).data['data']
                        profile.photos = (await axios.get(`${env.javaServerUrl}/file/${socket['userId']}/photo`)).data[
                           'data'
                        ]

                        socket.to('match-' + id).emit('match:new', profile)

                        // Send matched notification to user two
                        res = await axios.post(`${env.javaServerUrl}/notify`, {
                           ...req,
                           sourceUID: id,
                           targetUID: socket['userId'],
                        })

                        notification = res.data['data']
                        sio.of('/notify')
                           .to('notify-' + socket['userId'])
                           .emit('notification:new', notification)

                        profile = (await axios.get(`${env.javaServerUrl}/profile/id/${id}`)).data['data']
                        profile.photos = (await axios.get(`${env.javaServerUrl}/file/${id}/photo`)).data['data']

                        socket.to('match-' + socket['userId']).emit('match:new', profile)
                     } catch (err) {
                        console.log(err)
                     }
                  } catch (err) {
                     //
                  }
               }
            } catch (err) {
               console.log(err)
            }
         })
         .on('profile:react', async ({ id, name }: { id: number; name: string }) => {
            try {
               await axios.post(`${env.javaServerUrl}/superLike/${socket['userId']}/${id}`)

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
                  .emit('notification:success', {
                     title: 'Super Like 👍',
                     message: '🎉 You sent a super like to ' + name.split(' ')[0] + ' 🎉',
                  })

               sio.of('/notify')
                  .to('notify-' + id)
                  .emit('notification:new', notification)
            } catch {
               sio.of('/notify')
                  .to('notify-' + socket['userId'])
                  .emit('notification:error', {
                     title: 'Super Like 👍',
                     message: 'You already sent a super like to ' + name.split(' ')[0] + ' ❌',
                  })
            }
         })
         .on('unmatch', async ({ userId, name }: { userId: number; name: string }) => {
            try {
               await axios.put(`${env.javaServerUrl}/matchStatus`, {
                  userIdOne: socket['userId'],
                  userIdTwo: userId,
                  like: false,
               })

               sio.of('/notify')
                  .to('notify-' + socket['userId'])
                  .emit('notification:success', {
                     title: 'Unmatch',
                     message: 'You unmatched with ' + name.split(' ')[0] + ' ☹️',
                  })

               socket.to('match-' + userId).emit('match:remove', socket['userId'])
            } catch {
               sio.of('/notify')
                  .to('notify-' + socket['userId'])
                  .emit('notification:error', {
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
         .on('notification:get', async () => {
            const res = await axios.get(`${env.javaServerUrl}/notify/userId/${socket['userId']}`)
            const notifications = res.data.data
            socket.to('notify-' + socket['userId']).emit(
               'notifications',
               notifications.map((el: Notification) => {
                  if (el.eventType !== 'react') return el
                  delete el.sourceUID
                  return el
               }),
            )
         })
         .on('notification:seen', async ({ userId, notificationId }: { userId: number; notificationId: number }) => {
            if (userId !== socket['userId']) return
            await axios.get(`${env.javaServerUrl}/notify/seen/${userId}/${notificationId}`)
         })
         .on('notification:seenAll', async () => {
            await axios.get(`${env.javaServerUrl}/notify/seenAll/userId/${socket['userId']}`)
            socket.to('notify-' + socket['userId']).emit('notification:seenAll')
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

         if (!conversationId) return next()

         const conversation = (
            await api({
               url: `${env.javaServerUrl}/conversation/check/${conversationId}/${socket['userId']}`,
               method: 'GET',
            })
         ).data['data']

         socket['otherId'] = conversation['otherId'] as string

         next()
      } catch (err) {
         console.log(err)
      }
   })
   .on('connection', (socket: Socket) => {
      socket.join('chat-' + socket['userId'])

      socket
         .on('message:getMore', async (time: number) => {
            if (!socket.handshake.query['conversationId']) return

            try {
               const messages = (
                  await axios.get(`${env.javaServerUrl}/message/${socket.handshake.query['conversationId']}/${time}`)
               ).data['data']

               socket.to('chat-' + socket['userId']).emit('messages', messages)
            } catch {
               //
            }
         })
         .on('message:send', async (content: string) => {
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
                  .emit('message:new', message)
            } catch {
               //
            }
         })
         .on('message:react', async ({ messageId, react }: { messageId: number; react: string }) => {
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
                  .emit('message:react', message)
            } catch {
               //
            }
         })
         .on('message:delete', async (messageId: string) => {
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
               .emit('message:delete', message)
         })
         .on('message:typing', (state: boolean) => socket.to('chat-' + socket['otherId']).emit('message:typing', state))
         .on('disconnect', () => {
            socket.removeAllListeners()
         })
   })

sio.of('/report')
   .use(authMiddleware)
   .on('connection', (socket: Socket) => {
      if (socket['role'] !== 'user') socket.join('report-' + socket['role'])
      else socket.join('report-user-' + socket['userId'])

      socket
         .on(
            'report:request',
            async ({ reported, tag, additionalInfo }: { reported: number; tag: string; additionalInfo: string }) => {
               try {
                  const report: Report = (
                     await axios.post(`${env.javaServerUrl}/report`, {
                        reporter: socket['userId'],
                        reported,
                        tag,
                        additionalInfo,
                     })
                  ).data['data']

                  report.reporterProfile = (await axios.get(`${env.javaServerUrl}/profile/id/${report.reporter}`)).data[
                     'data'
                  ]
                  report.reportedProfile = (await axios.get(`${env.javaServerUrl}/profile/id/${report.reported}`)).data[
                     'data'
                  ]
                  report.reportedUserInfo = (await axios.get(`${env.javaServerUrl}/user/id/${report.reported}`)).data[
                     'data'
                  ]

                  socket.to('report-mod').to('report-admin').emit('report:new', report)

                  sio.of('/notify')
                     .to('notify-' + socket['userId'])
                     .emit('notification:success', {
                        title: 'Report',
                        message:
                           'Thank you for reporting the offending user so that PICKME can take action. We appreciate your contribution to the development of the application',
                     })
               } catch (err) {
                  sio.of('/notify')
                     .to('notify-' + socket['userId'])
                     .emit('notification:error', {
                        title: 'Report',
                        message: 'You have already reported this user. Please wait us to prcoess your submitted report',
                     })
               }
            },
         )
         .on(
            'report:resolve',
            async ({ reportId, action }: { reportId: number; action: 'ban' | 'warn' | 'decline' }) => {
               if (socket['role'] === 'user') return

               try {
                  const status = action === 'decline' ? 'decline' : 'approve'
                  const report: Report = (await axios.put(`${env.javaServerUrl}/report/${reportId}/${status}`)).data[
                     'data'
                  ]

                  report.reporterProfile = (await axios.get(`${env.javaServerUrl}/profile/id/${report.reporter}`)).data[
                     'data'
                  ]
                  report.reportedProfile = (await axios.get(`${env.javaServerUrl}/profile/id/${report.reported}`)).data[
                     'data'
                  ]
                  report.reportedUserInfo = (await axios.get(`${env.javaServerUrl}/user/id/${report.reported}`)).data[
                     'data'
                  ]
                  let message: string

                  switch (action) {
                     case 'ban':
                        await axios.put(`${env.javaServerUrl}/user`, {
                           userId: report.reported,
                           disabled: true,
                        })
                        message = `Your report about ${report.reportedProfile.name} was approved`
                        delete userCache.store['store'][
                           `${env.javaServerUrl}/user/email/${report.reportedUserInfo.email}`
                        ]
                        console.log(
                           userCache.store['store'],
                           userCache.store['store'][`${env.javaServerUrl}/user/email/${report.reportedUserInfo.email}`],
                           `${env.javaServerUrl}/user/email/${report.reportedUserInfo.email}`,
                        )
                        break
                     case 'warn':
                        await axios.put(`${env.javaServerUrl}/user`, {
                           userId: report.reported,
                           cautionTimes: report.reportedUserInfo.cautionTimes + 1,
                        })
                        message = `Your report about ${report.reportedProfile.name} was approved`

                        sio.of('/notify')
                           .to('notify-' + report.reported)
                           .emit(
                              'notification:new',
                              (
                                 await axios.post(`${env.javaServerUrl}/notify`, {
                                    time: new Date().getTime(),
                                    sourceUID: null,
                                    targetUID: report.reported,
                                    eventType: 'warn',
                                    seen: false,
                                    message: 'You have been warned from Moderator due to ' + report.tag.toUpperCase(),
                                 })
                              ).data['data'],
                           )
                        break
                     case 'decline':
                        message = `Your report about ${report.reportedProfile.name} was rejected`
                        break
                  }

                  const notification = (
                     await axios.post(`${env.javaServerUrl}/notify`, {
                        time: new Date().getTime(),
                        sourceUID: null,
                        targetUID: report.reporter,
                        eventType: 'info',
                        seen: false,
                        message,
                     })
                  ).data['data']

                  sio.of('/notify')
                     .to('notify-' + report.reporter)
                     .emit('notification:new', notification)

                  socket.to('report-mod').to('report-admin').emit('report:update', report)
               } catch (err) {
                  console.log(err)
               }
            },
         )
   })

export default function attach(server: http.Server) {
   sio.attach(server)
}
