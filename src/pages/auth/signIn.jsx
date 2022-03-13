import { getProviders, signIn } from 'next-auth/react'
import getConfig from 'next/config'
import Image from 'next/image'

const icons = {
   facebook: <Image src="/static/images/facebook.png" alt="Facebook" priority={true} width="40" height="40" />,
   google: <Image src="/static/images/google.png" alt="Google" priority={true} width="40" height="40" />,
   twitter: <Image src="/static/images/twitter.png" alt="Twitter" priority={true} width="40" height="40" />,
}

const {
   publicRuntimeConfig: { publicURL },
} = getConfig()

export default function SignIn({ providers }) {
   return (
      <div className="fixed flex items-center justify-center w-screen h-screen select-none">
         <Image
            className="z-0 object-cover object-center"
            src="/static/images/background.jpg"
            alt=""
            layout="fill"
            priority={true}
         />
         <div className="relative flex flex-col items-center p-8 bg-white rounded-xl z-1 gap-y-6">
            <Image
               className="object-contain object-center"
               src="/static/images/logo.png"
               alt="PickMe logo"
               priority={true}
               width="60"
               height="120"
            />
            <p className="text-2xl font-bold">GET STARTED</p>
            <div className="flex flex-col items-center gap-y-3">
               {Object.values(providers).map((provider) => (
                  <button
                     key={provider.id}
                     className="flex items-center py-1 pl-4 border-2 rounded-full w-72 gap-x-3 hover:opacity-70"
                     onClick={() => signIn(provider.id, { callbackUrl: new URL('/app', publicURL).href })}
                  >
                     {icons[provider.id]}
                     Continue with {provider.name}
                  </button>
               ))}
            </div>
            <a className="underline text-neutral-500" href="/terms">
               Have you read our Terms?
            </a>
         </div>
      </div>
   )
}

export async function getServerSideProps() {
   const providers = await getProviders()
   return {
      props: { providers },
   }
}
