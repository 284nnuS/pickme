/* eslint-disable no-unused-vars */
import { NextFunction, Request, Response } from 'express'
import { cache } from '../shared/cache'
import { getToken } from 'next-auth/jwt'
import { NextApiRequest, NextApiResponse } from 'next'
import NextAuth from 'next-auth'
import GoogleProvider from 'next-auth/providers/google'

const baseUrl = '/api/auth/'

export async function nextAuth(req: Request, res: Response, next: NextFunction) {
   if (!req.url.startsWith(baseUrl)) {
      return next()
   }

   req.query.nextauth = req.url.slice(baseUrl.length).replace(/\?.*/, '').split('/')

   NextAuth(req as unknown as NextApiRequest, res as unknown as NextApiResponse, {
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
         async signIn({ user, account, profile, email, credentials }) {
            return true
         },
         async redirect({ url, baseUrl }) {
            if (url.startsWith(baseUrl)) return url
            else if (url.startsWith('/')) return new URL(url, baseUrl).toString()
            return baseUrl
         },
         async session({ session, user, token }) {
            return session
         },
      },
      secret: process.env.SECRET_KEY,
   })
}

export async function authorize(req: Request, res: Response, next: NextFunction) {
   const token = await getToken({ req: req as unknown as NextApiRequest, secret: process.env.SECRET_KEY })

   if (!token) return next()

   //Get User Role
   const getRole = (email: string): string => {
      return 'user'
   }

   res.locals.token = token
   res.locals.userRole = cache(getRole, token.email)

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
