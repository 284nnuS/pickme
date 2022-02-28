import { Express } from 'express'
import { NextApiHandler } from 'next'
import { NextServer } from 'next/dist/server/next'
import { permit } from '../security'

export default function route(app: Express, nextApp: NextServer) {
   const nextHandler: NextApiHandler = nextApp.getRequestHandler()

   // eslint-disable-next-line @typescript-eslint/no-explicit-any
   const handler = (req: any, res: any) => nextHandler(req, res)

   app.all('/api/*', handler)

   // app.all('/auth/signUp/', permit('none'), handler)

   app.all('/app/*', permit('user'), handler)

   app.all('/*', handler)
}
