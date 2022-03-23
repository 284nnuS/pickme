import { Divider, Menu } from '@mantine/core'
import { signOut } from 'next-auth/react'
import { useRouter } from 'next/router'
import { AiFillInfoCircle, AiFillWarning } from 'react-icons/ai'
import { FaUserAlt } from 'react-icons/fa'
import { IoIosHelpCircle } from 'react-icons/io'
import { MdReport } from 'react-icons/md'
import { RiSettings4Fill } from 'react-icons/ri'
import { VscSignOut } from 'react-icons/vsc'

function AvatarMenu({ profile, role, control }: { profile: UserProfile; role: string; control: JSX.Element }) {
   const router = useRouter()

   return (
      <Menu
         trigger="hover"
         delay={500}
         control={control}
         classNames={{
            itemHovered: 'bg-slate-100',
         }}
         placement="end"
      >
         <Menu.Item icon={<RiSettings4Fill className="w-5 h-5 text-emerald-600" />}>Preferences</Menu.Item>
         <Menu.Item
            icon={<FaUserAlt className="w-4 h-4 mr-1 text-blue-600" />}
            onClick={() => router.push(`/app/profile/${profile.userId}`)}
         >
            View profile
         </Menu.Item>
         <Divider />
         <Menu.Item icon={<IoIosHelpCircle className="w-5 h-5 text-lime-600" />}>Help</Menu.Item>
         <Menu.Item icon={<AiFillWarning className="w-5 h-5 text-yellow-600" />}>Report a problem</Menu.Item>
         <Menu.Item icon={<AiFillInfoCircle className="w-5 h-5 text-cyan-600" />}>About</Menu.Item>
         {role === 'mod' && (
            <>
               <Divider />
               <Menu.Item
                  icon={<MdReport className="w-5 h-5 text-rose-600" />}
                  onClick={() => router.push('/manage/reports')}
               >
                  Report List
               </Menu.Item>
            </>
         )}
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
   )
}

export default AvatarMenu
