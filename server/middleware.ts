/* eslint-disable no-unused-vars */
import { Request, Response } from 'express'
import { needAuthentication } from './security'
import { getToken } from 'next-auth/jwt'
import { NextApiRequest, NextApiResponse } from 'next'
import NextAuth from 'next-auth'
import GoogleProvider from 'next-auth/providers/google'

const baseUrl = '/api/auth/'

export async function auth(req: Request, res: Response, next: () => void) {
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

export async function filter(req: Request, res: Response, next: () => void) {
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
