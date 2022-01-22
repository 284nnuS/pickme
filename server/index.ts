import http from 'http'
import next, { NextApiHandler } from 'next'
import express, { Express } from 'express'
import { createProxyMiddleware } from 'http-proxy-middleware'
import middleware from './middleware'
import helmet from 'helmet'
import attach from './websocket'
import cookieParser from 'cookie-parser'

const dev = process.env.NODE_ENV !== 'production'
const port = parseInt(process.env.PORT || '3000', 10)

const app: Express = express()
const nextApp = next({ dev })
const server = http.createServer(app)
const nextHandler: NextApiHandler = nextApp.getRequestHandler()

app.use(
   helmet({
      contentSecurityPolicy: false,
   }),
)
app.use(cookieParser())
app.use(middleware)

attach(server)

nextApp.prepare().then(async () => {
   app.all(
      '/api/restful/*',
      createProxyMiddleware({
         target: 'http://localhost:3001',
         changeOrigin: true,
         pathRewrite: { '^/api/restful': '' },
      }),
   )

   app.all('/*', (req: any, res: any) => nextHandler(req, res))

   server.listen(port, () => {
      console.log(`> Ready on http://localhost:${port}`)
   })
})
