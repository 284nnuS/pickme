import http from 'http'
import next from 'next'
import express, { Express } from 'express'
import { authorize } from './security'
import attach from './websocket'
import cookieParser from 'cookie-parser'
import route from './routes'

const dev = process.env.NODE_ENV !== 'production'
const port = parseInt(process.env.PORT || '3000', 10)

const app: Express = express()
const nextApp = next({ dev })
const server = http.createServer(app)

app.use(express.json({ limit: '50mb' }))
app.use(express.urlencoded({ limit: '50mb' }))
app.use(cookieParser())
app.use(['/', '/app/*', '/api/*', '/auth/*'], authorize)
attach(server)

nextApp.prepare().then(async () => {
   route(app, nextApp)

   server.listen(port, () => {
      console.log(`> Listening on ${port}`)
   })
})
