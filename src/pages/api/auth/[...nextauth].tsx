/* eslint-disable no-unused-vars */
import NextAuth from 'next-auth'
import { JWT, JWTDecodeParams, JWTEncodeParams } from 'next-auth/jwt'
import GoogleProvider from 'next-auth/providers/google'

export default NextAuth({
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
      verifyRequest: '/auth/verifyRequest',
      newUser: '/auth/newUser',
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
      async jwt({ token, user, account, profile, isNewUser }) {
         console.log(isNewUser)
         return token
      },
   },
   secret: process.env.SECRET_KEY,
})
