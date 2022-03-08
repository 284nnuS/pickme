/* eslint-disable @typescript-eslint/no-unused-vars */
import { NextFunction, Request, Response } from 'express'
import { getToken, JWT } from 'next-auth/jwt'
import { NextApiRequest, NextApiResponse } from 'next'
import { getServerSession, Session } from 'next-auth'
import { has } from '../shared/utils'
import nextAuthOptions from '../shared/nextAuthOptions'

export async function authorize(req: Request, res: Response, next: NextFunction) {
   if (req.url.startsWith('/_next') || req.url.startsWith('/static') || req.url.startsWith('/api/auth')) return next()

   const token = await getToken({ req: req as unknown as NextApiRequest, secret: process.env.SECRET_KEY })

   if (!token) return next()

   const session = await getServerSession(
      { req: req as unknown as NextApiRequest, res: res as unknown as NextApiResponse },
      nextAuthOptions,
   )

   if (!req.url.startsWith('/auth/signUp') && !req.url.startsWith('/api/signUp') && !has(session, 'userInfo.userId')) {
      res.redirect('/auth/signUp')
      return
   }

   res.locals.session = session
   res.locals.token = token

   next()
}

export function permit(...roles: string[]): (req: Request, res: Response, next: NextFunction) => void {
   return (req: Request, res: Response, next: NextFunction) => {
      const session: Session = res.locals.session
      const token: JWT = res.locals.token

      if (!token) {
         //Unauthenticated
         res.redirect('/auth/signIn')
         return
      }

      if (!has(session, 'userInfo.userId')) {
         if (roles.includes('none')) {
            next()
            return
         }
         res.redirect('/auth/signUp')
         return
      } else if (roles.includes('none')) {
         res.redirect('/')
         return
      }

      const userInfo: UserInfo = session['userInfo'] as UserInfo

      if (!roles.includes(userInfo.role)) {
         //Unauthorized
         res.status(403).send()
         return
      }
      next()
   }
}
