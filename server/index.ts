import http from 'http'
import next, { NextApiHandler } from 'next'
import express, { Express } from 'express'
import { createProxyMiddleware } from 'http-proxy-middleware'
import { auth, filter } from './middleware'
import helmet from 'helmet'
import attach from './websocket'
import bodyParser from 'body-parser'
import cookieParser from 'cookie-parser'

const dev = process.env.NODE_ENV !== 'production'
const port = parseInt(process.env.PORT || '3000', 10)

const app: Express = express()
const nextApp = next({ dev })
const server = http.createServer(app)
const nextHandler: NextApiHandler = nextApp.getRequestHandler()

process.env['NEXTAUTH_URL'] = process.env.NEXT_PUBLIC_URL

app.use(
   helmet({
      contentSecurityPolicy: !dev,
   }),
)
app.use(bodyParser.urlencoded())
app.use(bodyParser.json())
app.use(cookieParser())
app.use(auth)
app.use(filter)

attach(server)

nextApp.prepare().then(async () => {
   app.all(
      '/api/restful/*',
      createProxyMiddleware({
         target: process.env.JAVA_SERVER_URL,
         changeOrigin: true,
         pathRewrite: { '^/api/restful': '' },
      }),
   )

   app.all('/*', (req: any, res: any) => nextHandler(req, res))

   server.listen(port, () => {
      console.log(`> Listening on ${port}`)
   })
})
