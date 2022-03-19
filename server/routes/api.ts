import { Express } from 'express'
import axios from 'axios'
import env from '../../shared/env'
import proxy from 'express-http-proxy'
import { JWT } from 'next-auth/jwt'

export default function routeAPI(app: Express) {
   const middleware = proxy(process.env.JAVA_SERVER_URL, {
      proxyReqPathResolver: function (req) {
         req.url = req.url.slice('/api/restful'.length)
         return req.url
      },
   })

   app.all('/api/restful/file/*', middleware)
   app.get('/api/restful/interest', middleware)
   app.put('/api/restful/profile', middleware)

   app.post('/api/signUp', async (req, res) => {
      const token: JWT = res.locals['token']
      const obj = req.body

      try {
         const userInfo: UserInfo = (
            await axios.post(`${env.javaServerUrl}/user`, {
               email: token.email,
               role: 'user',
            })
         ).data['data']

         await axios.post(`${env.javaServerUrl}/profile`, {
            userId: userInfo.userId,
            name: obj['name'],
            avatar: token.picture,
            gender: obj['gender'],
            bio: obj['bio'],
            birthday: obj['birthday'],
            interests: obj['interests'],
         })

         res.status(200).send({
            data: userInfo.userId,
         })
      } catch (err) {
         res.status(400).send()
      }
   })
}
