import { Avatar, Divider, Image, Menu, Tabs } from '@mantine/core'
import axios from 'axios'
import classNames from 'classnames'
import { signOut } from 'next-auth/react'
import Link from 'next/link'
import { useEffect, useRef, useState } from 'react'
import { AiFillInfoCircle, AiFillMobile, AiFillWarning } from 'react-icons/ai'
import { BsFillFlagFill } from 'react-icons/bs'
import { FaUserAlt, FaUserFriends } from 'react-icons/fa'
import { IoIosHelpCircle, IoMdArrowBack } from 'react-icons/io'
import { MdLocationOn, MdOutlinePhotoCameraBack, MdWebStories } from 'react-icons/md'
import { RiSettings4Fill } from 'react-icons/ri'
import { VscSignOut } from 'react-icons/vsc'
import { useMedia, useWindowScroll } from 'react-use'
import env from '~/shared/env'
import { ChipsInProfile, EditProfile, NotificationBox, ProfileStatus } from '~/src/components'

function Profile({
   yourProfile,
   initProfile,
   conversationId,
   defaultInterests,
   isYourProfile,
}: {
   yourProfile: UserProfile
   initProfile: UserProfile
   conversationId?: number
   defaultInterests: InterestChip[]
   isYourProfile: boolean
}) {
   const [profile, setProfile] = useState(null)

   useEffect(() => setProfile(initProfile), [initProfile])

   const { y } = useWindowScroll()

   if (!profile) return <></>

   return (
      <div className="flex flex-col items-center min-h-screen">
         <div className="absolute top-0 bottom-0 left-0 right-0 -z-50">
            <Image src="/static/images/profileBackground.jpg" alt="" />
         </div>
         <div
            className={classNames('fixed z-50 flex items-center w-full px-6 py-3 gap-x-4')}
            style={{
               backgroundColor: `rgba(5,150,105,${Math.min(1.0, (y - 20.0) / 20.0)})`,
            }}
         >
            <Link href="/app" passHref>
               <a className="w-10 h-10 rounded-full hover:bg-slate-300/60" onClick={() => window.history.back()}>
                  <IoMdArrowBack className="w-full h-full text-white" />
               </a>
            </Link>
            <div className="flex-grow"></div>
            <NotificationBox yourId={yourProfile.userId} inProfile={true} />
            <Menu
               trigger="hover"
               delay={500}
               control={
                  <button className="rounded-full">
                     <Avatar src={yourProfile.avatar} alt={yourProfile.name} radius="xl" size="md" />
                  </button>
               }
               classNames={{
                  itemHovered: 'bg-slate-100',
               }}
               placement="end"
            >
               <Menu.Item icon={<RiSettings4Fill className="w-5 h-5 text-emerald-600" />}>Preferences</Menu.Item>
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
         <div className="relative max-w-[calc(1280px-3rem)] mt-48 w-full bg-white rounded-lg flex flex-col mb-20 z-0">
            <div className="absolute left-0 right-0 z-10 flex justify-center h-48 -top-24">
               <Avatar size={192} src={profile.avatar} radius="md" alt={profile.name} />
            </div>
            <div className="items-center justify-between hidden md:flex">
               <div className="w-[calc(50%-6rem)] pt-10 flex px-4 justify-center md:gap-x-3 lg:gap-x-12 flex-wrap">
                  {Object.entries({
                     Matches: profile.matches.length,
                     Likes: profile.likes,
                     Photos: profile.photos.length,
                     Interests: profile.interests.length,
                  }).map(([label, value]) => {
                     return (
                        <div key={label} className="flex flex-col items-center w-16">
                           <div className="text-3xl font-semibold text-slate-700">{value}</div>
                           <div className="text-sm text-slate-500">{label}</div>
                        </div>
                     )
                  })}
               </div>
               <div className="w-[calc(50%-6rem)] pt-10 flex px-8 justify-end gap-x-8 z-10">
                  <Link href={`/app/chat/${conversationId}`} passHref>
                     <a
                        className={classNames(
                           'w-32 h-10 text-lg font-semibold leading-10 text-center text-white uppercase rounded-md bg-gradient-to-r from-indigo-300 to-cyan-300 hover:to-indigo-300 hover:from-cyan-300',
                           isYourProfile && 'filter grayscale cursor-default',
                        )}
                        onClick={(e) => isYourProfile && e.preventDefault()}
                     >
                        Message
                     </a>
                  </Link>
                  {isYourProfile ? (
                     <EditProfile
                        key={profile}
                        initProfile={profile}
                        defaultInterests={defaultInterests}
                        onEditedSuccess={(n) => setProfile(n)}
                     />
                  ) : (
                     <button className="w-10 h-10 p-1.5 rounded-full hover:bg-slate-300/60">
                        <BsFillFlagFill className="w-full h-full text-red-700" />
                     </button>
                  )}
               </div>
            </div>
            <div className="flex flex-col items-center w-full px-3 mt-[7rem] md:mt-3 gap-y-2">
               <div className="text-3xl font-bold text-center">{profile.name}</div>
               <div className="flex flex-col items-center gap-y-2">
                  {profile.address && (
                     <div className="flex items-center gap-x-3">
                        <MdLocationOn className="w-6 h-6 text-blue-600" />
                        <span className="text-sm text-slate-700">{profile.address}</span>
                     </div>
                  )}
                  {profile.phone && (
                     <div className="flex items-center gap-x-3">
                        <AiFillMobile className="w-6 h-6 p-0.5 text-blue-600" />
                        <span className="text-sm text-slate-700">{profile.phone}</span>
                     </div>
                  )}
                  {profile.interests.length > 0 && (
                     <ChipsInProfile
                        values={defaultInterests.filter((el) => profile.interests.indexOf(el.name) > -1)}
                     />
                  )}
                  <ProfileStatus
                     key={profile}
                     editable={isYourProfile}
                     userId={isYourProfile ? profile.userId : -1}
                     initStatusEmoji={profile.statusEmoji}
                     initStatusText={profile.statusText}
                     onEditedSuccess={(emoji, text) =>
                        setProfile((c) => {
                           return {
                              ...c,
                              statusEmoji: emoji,
                              statusText: text,
                           }
                        })
                     }
                  />
                  <div className="flex flex-col items-center gap-y-3 md:hidden ">
                     <div className="flex justify-center w-full px-2 pt-6">
                        {Object.entries({
                           Matches: profile.matches.length,
                           Likes: profile.likes,
                           Photos: profile.photos.length,
                           Interests: profile.interests.length,
                        }).map(([label, value]) => {
                           return (
                              <div key={label} className="flex flex-col items-center w-16">
                                 <div className="text-3xl font-semibold text-slate-700">{value}</div>
                                 <div className="text-sm text-slate-500">{label}</div>
                              </div>
                           )
                        })}
                     </div>
                     {isYourProfile || (
                        <Link href={`/app/chat/${conversationId}`} passHref>
                           <a
                              className="w-32 h-10 text-lg font-semibold leading-10 text-center text-white uppercase rounded-md bg-gradient-to-r from-indigo-300 to-cyan-300 hover:to-indigo-300 hover:from-cyan-300"
                              onClick={(e) => isYourProfile && e.preventDefault()}
                           >
                              Message
                           </a>
                        </Link>
                     )}
                     {isYourProfile ? (
                        <EditProfile
                           key={profile}
                           initProfile={profile}
                           defaultInterests={defaultInterests}
                           onEditedSuccess={(n) => setProfile(n)}
                        />
                     ) : (
                        <button className="w-10 h-10 p-1.5 rounded-full hover:bg-slate-300/60">
                           <BsFillFlagFill className="w-full h-full text-red-700" />
                        </button>
                     )}
                  </div>
               </div>
               <div className="w-full p-6 mt-5 text-lg text-center border-t-2 border-t-slate-300 text-slate-600">
                  {profile.bio}
               </div>
            </div>
            <Tabs
               className="p-2"
               variant="unstyled"
               styles={{
                  tabActive: {
                     color: '#2563EB !important',
                  },
               }}
               classNames={{
                  tabsList: 'flex justify-center',
                  tabControl: 'text-slate-600',
                  tabLabel: 'text-xl',
               }}
            >
               <Tabs.Tab label="Posts" icon={<MdWebStories className="w-5 h-5" />}>
                  <div className="flex items-center justify-center w-full p-6 text-3xl font-semibold text-center text-slate-500">
                     No posts
                  </div>
               </Tabs.Tab>
               {isYourProfile && (
                  <Tabs.Tab label="Matches" icon={<FaUserFriends className="w-5 h-5" />}>
                     <div className="flex flex-wrap justify-around w-full px-12 py-8 gap-y-3">
                        {profile.matches.map((el) => {
                           return (
                              <Link href={`/app/profile/${el.userId}`} key={el.userId}>
                                 <a className="flex items-center w-[20rem] gap-x-5">
                                    <Image
                                       src={el.avatar}
                                       alt={el.name}
                                       radius={10}
                                       width={100}
                                       height={100}
                                       fit="contain"
                                    />
                                    <div className="text-xl">{el.name}</div>
                                 </a>
                              </Link>
                           )
                        })}
                     </div>
                  </Tabs.Tab>
               )}
               <Tabs.Tab label="Gallery" icon={<MdOutlinePhotoCameraBack className="w-5 h-5" />}>
                  <div className="grid w-full grid-cols-1 gap-2 p-12 md:grid-cols-5 lg:grid-cols-6">
                     {profile.photos.map((el, i) => {
                        return (
                           <Link
                              href={`/app/imgViewer/?profileId=${profile.userId}&bucketName=${el.bucketName}&currentPhotoName=${el.fileName}`}
                              passHref
                              key={i}
                           >
                              <a
                                 className="aspect-[2/3] bg-center bg-cover bg-no-repeat rounded-md border-2 border-slate-200"
                                 style={{
                                    backgroundImage: `url('/api/restful/file/${profile.userId}/${el.bucketName}/${el.fileName}')`,
                                 }}
                              ></a>
                           </Link>
                        )
                     })}
                  </div>
               </Tabs.Tab>
            </Tabs>
         </div>
      </div>
   )
}

export async function getServerSideProps({ query, res }) {
   const { locals } = res

   if (!locals.session) {
      return {
         notFound: true,
      }
   }

   const userInfo: UserInfo = locals.session.userInfo
   const userId = userInfo.userId

   const profileId = +query.profileId

   let profile: UserProfile
   let yourProfile: UserProfile
   // eslint-disable-next-line @typescript-eslint/no-explicit-any
   let defaultInterests: any[]
   let conversationId: number = null

   try {
      profile = (await axios.get(`${env.javaServerUrl}/profile/id/${profileId}`)).data['data']
      profile.photos = (await axios.get(`${env.javaServerUrl}/file/${profileId}/photo`)).data['data']
      if (profileId !== userId) {
         yourProfile = (await axios.get(`${env.javaServerUrl}/profile/id/${userId}`)).data['data']
         conversationId = (await axios.get(`${env.javaServerUrl}/conversation/${userId}/${profileId}`)).data['data'][
            'conversationId'
         ]
      } else yourProfile = profile
      defaultInterests = (await axios.get(`${env.javaServerUrl}/interest`)).data['data']
   } catch {
      return {
         notFound: true,
      }
   }

   const isYourProfile = userId === profileId

   if (!isYourProfile)
      profile = {
         ...profile,
         matches: profile.matches.map(() => {
            return {
               userId: null,
               name: null,
               avatar: null,
            }
         }),
      }

   return {
      props: {
         yourProfile,
         initProfile: profile,
         conversationId,
         defaultInterests: defaultInterests.map((el) => {
            return {
               name: el['interestName'],
               description: el['description'],
            }
         }),
         isYourProfile: userId === profileId,
      },
   }
}

export default Profile
