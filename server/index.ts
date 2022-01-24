import http from 'http'
import next from 'next'
import express, { Express } from 'express'
import { nextAuth, authorize } from './security'
import helmet from 'helmet'
import attach from './websocket'
import bodyParser from 'body-parser'
import cookieParser from 'cookie-parser'
import route from './routes'

const dev = process.env.NODE_ENV !== 'production'
const port = parseInt(process.env.PORT || '3000', 10)

const app: Express = express()
const nextApp = next({ dev })
const server = http.createServer(app)

app.use(
   helmet({
      contentSecurityPolicy: !dev,
   }),
)

app.use(bodyParser.json())
app.use(cookieParser())
app.use(nextAuth)
app.use(authorize)

attach(server)

nextApp.prepare().then(async () => {
   route(app, nextApp)

   server.listen(port, () => {
      console.log(`> Listening on ${port}`)
   })
})
