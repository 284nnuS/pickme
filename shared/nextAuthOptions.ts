import GoogleProvider from 'next-auth/providers/google'
import FacebookProvider from 'next-auth/providers/facebook'
import axios from 'axios'
import { NextAuthOptions, Session, User } from 'next-auth'
import { JWT } from 'next-auth/jwt'

export const getUserInfo = async (email: string) => {
   try {
      return await (
         await axios.get(`${process.env.JAVA_SERVER_URL}/user/email/${encodeURI(email)}`)
      ).data
   } catch (e) {
      return e.response.data
   }
}

const nextAuthOptions: NextAuthOptions = {
   providers: [
      FacebookProvider({
         clientId: process.env.FACEBOOK_CLIENT_ID,
         clientSecret: process.env.FACEBOOK_CLIENT_SECRET,
         httpOptions: { timeout: 40000 },
      }),
      GoogleProvider({
         clientId: process.env.GOOGLE_CLIENT_ID,
         clientSecret: process.env.GOOGLE_CLIENT_SECRET,
         httpOptions: { timeout: 40000 },
      }),
   ],
   pages: {
      signIn: '/auth/signIn',
   },
   callbacks: {
      async signIn({ user }: { user: User }) {
         console.log(user)
         return true
      },
      async redirect({ url, baseUrl }) {
         if (url.startsWith(baseUrl)) return url
         else if (url.startsWith('/')) return new URL(url, baseUrl).toString()
         return baseUrl
      },
      async session({ session, token }: { session: Session; token: JWT; user: User }) {
         const res = await getUserInfo(token.email)
         if (res && 'data' in res)
            session.user = {
               email: token.email,
               role: res['data'].role,
               name: res['data'].name,
               id: res['data'].userId,
               avatar: res['data'].avatar,
            }
         return session
      },
   },
   secret: process.env.SECRET_KEY,
}

export default nextAuthOptions
