import { Avatar } from '@mantine/core'
import classNames from 'classnames'
import Link from 'next/link'
import { RiMoreFill } from 'react-icons/ri'
import AvatarMenu from './AvatarMenu'

function SidebarHeader({ userProfile, tab, role }: { userProfile: UserProfile; tab: string; role: string }) {
   return (
      <>
         <div className="w-full h-[6rem] bg-gradient-to-r from-teal-600 to-cyan-600 flex p-5 items-center gap-x-3">
            <Avatar src={userProfile.avatar} radius="xl" size="lg" alt={userProfile.name} />
            <div className="ml-2 text-2xl font-bold text-white">{userProfile.name}</div>
            <div className="flex-grow"></div>
            <AvatarMenu
               profile={userProfile}
               role={role}
               control={
                  <button className="w-10 h-10 p-2 bg-white rounded-full">
                     <RiMoreFill className="w-full h-full text-teal-600" />
                  </button>
               }
            />
         </div>

         <div className="w-full">
            <div className="flex justify-around pt-3 pb-1">
               <Link href="/app" passHref>
                  <a
                     className={classNames(
                        'w-24 text-xl font-semibold text-center text-teal-700 no-underline border-b-2',
                        tab === 'matches' ? 'border-b-teal-700' : ' border-b-transparent hover:border-b-teal-800',
                     )}
                  >
                     Matches
                  </a>
               </Link>
               <Link href="/app/chat" passHref>
                  <a
                     className={classNames(
                        'w-24 text-xl font-semibold text-center text-teal-700 no-underline border-b-2',
                        tab === 'messages' ? 'border-b-teal-700' : ' border-b-transparent hover:border-b-teal-800',
                     )}
                  >
                     Messages
                  </a>
               </Link>
            </div>
         </div>
      </>
   )
}

export default SidebarHeader
