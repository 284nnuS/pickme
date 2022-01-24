import { Express } from 'express'
import routeApp from './app'
import routeAPI from './api'
import { NextServer } from 'next/dist/server/next'

export default function route(app: Express, nextApp: NextServer) {
   routeAPI(app)
   routeApp(app, nextApp)
}
