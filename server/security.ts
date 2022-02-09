/* eslint-disable @typescript-eslint/no-unused-vars */
import { NextFunction, Request, Response } from 'express'
import { getToken, JWT } from 'next-auth/jwt'
import { NextApiRequest, NextApiResponse } from 'next'
import NextAuth, { Account, getServerSession, Profile, Session, User } from 'next-auth'
import GoogleProvider from 'next-auth/providers/google'

const baseUrl = '/api/auth/'

const nextAuthOptions = {
   providers: [
      GoogleProvider({
         clientId: process.env.GOOGLE_CLIENT_ID,
         clientSecret: process.env.GOOGLE_CLIENT_SECRET,
      }),
   ],
   pages: {
      signIn: '/auth/signIn',
      signOut: '/auth/signOut',
      error: '/auth/error',
   },
   callbacks: {
      async signIn({
         user,
         account,
         profile,
         email,
         credentials,
      }: {
         user: User
         account: Account
         profile: Profile
         email
         credentials
      }) {
         return true
      },
      async redirect({ url, baseUrl }) {
         if (url.startsWith(baseUrl)) return url
         else if (url.startsWith('/')) return new URL(url, baseUrl).toString()
         return baseUrl
      },
      async session({ session, user, token }: { session: Session; user: User; token: JWT }) {
         session.user.role = 'user'

         return session
      },
   },
   secret: process.env.SECRET_KEY,
}

export async function nextAuth(req: Request, res: Response, next: NextFunction) {
   if (!req.url.startsWith(baseUrl)) {
      return next()
   }

   req.query.nextauth = req.url.slice(baseUrl.length).replace(/\?.*/, '').split('/')

   NextAuth(req as unknown as NextApiRequest, res as unknown as NextApiResponse, nextAuthOptions)
}

export async function authorize(req: Request, res: Response, next: NextFunction) {
   const token = await getToken({ req: req as unknown as NextApiRequest, secret: process.env.SECRET_KEY })

   if (!token) return next()

   const session = await getServerSession(
      { req: req as unknown as NextApiRequest, res: res as unknown as NextApiResponse },
      nextAuthOptions,
   )

   res.locals.token = token
   res.locals.userRole = session.user.role

   next()
}

export function permit(...roles: string[]): (req: Request, res: Response, next: NextFunction) => void {
   return (req: Request, res: Response, next: NextFunction) => {
      if (!res.locals.token) {
         //Unauthenticated
         res.redirect('/auth/signIn')
         return
      }
      if (!roles.includes(res.locals.userRole)) {
         //Unauthorized
         res.status(403).send()
         return
      }
      next()
   }
}
