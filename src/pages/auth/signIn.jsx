import { getProviders, signIn } from 'next-auth/react'
import getConfig from 'next/config'
import { FcGoogle } from 'react-icons/fc'

const {
   publicRuntimeConfig: { publicURL },
} = getConfig()

const icons = {
   google: <FcGoogle className="w-full h-full" />,
}

export default function SignIn({ providers }) {
   return (
      <div className="flex justify-center items-center w-screen h-screen">
         {Object.values(providers).map((provider) => (
            <div key={provider.name} className="border rounded-2xl">
               <button
                  className="flex items-center justify-center py-2 w-72"
                  onClick={() => signIn(provider.id, { callbackUrl: new URL('/app', publicURL).href })}
               >
                  <div className="w-10 h-10">{icons[provider.id]}</div>
                  <span>Continue with {provider.name}</span>
               </button>
            </div>
         ))}
      </div>
   )
}

export async function getServerSideProps() {
   const providers = await getProviders()
   return {
      props: { providers },
   }
}
