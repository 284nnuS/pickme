import { Express } from 'express'
import axios from 'axios'
import env from '../../shared/env'

export default function routeAPI(app: Express) {
   // const middleware = createProxyMiddleware({
   //    target: process.env.JAVA_SERVER_URL,
   //    changeOrigin: true,
   //    pathRewrite: { '^/api/restful': '' },
   // })

   // app.all('/api/restful/*', middleware)

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
