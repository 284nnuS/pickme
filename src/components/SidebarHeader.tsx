import { Avatar, Divider, Image, Menu } from '@mantine/core'
import classNames from 'classnames'
import { signOut } from 'next-auth/react'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { AiFillInfoCircle, AiFillWarning } from 'react-icons/ai'
import { FaUserAlt } from 'react-icons/fa'
import { IoIosHelpCircle } from 'react-icons/io'
import { RiMoreFill, RiSettings4Fill } from 'react-icons/ri'
import { VscSignOut } from 'react-icons/vsc'
import { useMedia } from 'react-use'
import NotificationBox from './NotificationBox'

function SidebarHeader({ userProfile, tab }: { userProfile: UserProfile; tab: string }) {
   const router = useRouter()

   const isWide = useMedia('(min-width: 768px)')

   return (
      <>
         <div className="w-full h-[6rem] bg-gradient-to-r from-teal-600 to-cyan-600 flex p-5 items-center gap-x-3">
            <Avatar src={userProfile.avatar} radius="xl" size="lg" alt={userProfile.name} />
            <div className="ml-2 text-2xl font-bold text-white">{userProfile.name}</div>
            <div className="flex-grow"></div>
            {isWide || <NotificationBox yourId={userProfile.userId} inProfile={true} gutter={27} />}
            <Menu
               trigger="hover"
               delay={500}
               control={
                  <button className="w-10 h-10 p-2 bg-white rounded-full">
                     <RiMoreFill className="w-full h-full text-teal-600" />
                  </button>
               }
               classNames={{
                  itemHovered: 'bg-slate-100',
               }}
               placement="end"
            >
               <Menu.Item icon={<RiSettings4Fill className="w-5 h-5 text-emerald-600" />}>Preferences</Menu.Item>
               <Menu.Item
                  icon={<FaUserAlt className="w-4 h-4 mr-1 text-blue-600" />}
                  onClick={() => router.push(`/app/profile/${userProfile.userId}`)}
               >
                  View profile
               </Menu.Item>
               <Divider />
               <Menu.Item icon={<IoIosHelpCircle className="w-5 h-5 text-lime-600" />}>Help</Menu.Item>
               <Menu.Item icon={<AiFillWarning className="w-5 h-5 text-yellow-600" />}>Report a problem</Menu.Item>
               <Menu.Item icon={<AiFillInfoCircle className="w-5 h-5 text-cyan-600" />}>About</Menu.Item>
               <Divider />
               <Menu.Item
                  icon={<VscSignOut className="w-5 h-5 text-red-600" />}
                  onClick={() =>
                     signOut({
                        callbackUrl: '/',
                        redirect: true,
                     })
                  }
               >
                  Sign Out
               </Menu.Item>
            </Menu>
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
