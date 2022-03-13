import { Express } from 'express'
import axios from 'axios'
import env from '../../shared/env'
import proxy from 'express-http-proxy'

export default function routeAPI(app: Express) {
   const middleware = proxy(process.env.JAVA_SERVER_URL, {
      proxyReqPathResolver: function (req) {
         req.url = req.url.slice('/api/restful'.length)
         return req.url
      },
   })

   app.get('/api/restful/media/*', middleware)
   app.get('/api/restful/interest', middleware)

   app.post('/api/signUp', async (req, res) => {
      const email = res.locals.token.email

      const json = {
         ...req.body,
         email,
         cautionTimes: 0,
         avatar: res.locals.token.picture,
         role: 'user',
      }
      const result = await axios.post(`${env.javaServerUrl}/user`, json)

      res.status(result.status).json(result.data)
   })
}
