import { Request, Response } from 'express'
import { needAuthentication } from '../configs/security'
import { getToken } from 'next-auth/jwt'
import { NextApiRequest } from 'next'

export default async function middleware(req: Request, res: Response, next: () => void) {
   const token = await getToken({ req: req as unknown as NextApiRequest, secret: process.env.SECRET_KEY })

   if (!token) {
      for (const rgx of needAuthentication)
         if (req.url.match(rgx)) {
            res.redirect('/')
            return
         }
   }
   next()
}
