import { Modal } from '@mantine/core'
import { DatePicker } from '@mantine/dates'
import axios from 'axios'
import { diff } from 'deep-object-diff'
import { useEffect, useRef, useState } from 'react'
import { AiFillMobile } from 'react-icons/ai'
import { FaBirthdayCake, FaUser } from 'react-icons/fa'
import { MdLocationOn } from 'react-icons/md'
import BlobImage from './BlobImage'
import ChipsInProfile from './ChipsInProfile'
import ChipsModal from './ChipsModal'
import { fromUint8Array } from 'js-base64'

function EditProfile({
   initProfile,
   defaultInterests,
   onEditedSuccess,
}: {
   initProfile: UserProfile
   defaultInterests: InterestChip[]
   onEditedSuccess: (newProfile: UserProfile) => void
}) {
   const [profile, setProfile] = useState(initProfile)

   const [uploadBlob, setUploadBlob] = useState<globalThis.File>(null)

   const [opened, setOpened] = useState(false)
   const [avatarModalOpened, setAvatarModalOpened] = useState(false)
   const [bioModalOpened, setBioModalOpened] = useState(false)
   const [chipsModalOpened, setChipsModalOpened] = useState(false)
   const [infosModalOpened, setInfosModalOpened] = useState(false)

   const fileInputRef = useRef<HTMLInputElement>()

   const [chips, setChips] = useState(defaultInterests.filter((el) => profile.interests.indexOf(el.name) > -1))

   useEffect(() => {
      setProfile((c) => {
         return {
            ...c,
            interests: chips.map((el) => el.name),
         }
      })
   }, [chips])

   return (
      <>
         <button className="w-10 h-10 p-1 rounded-full hover:bg-slate-300/40" onClick={() => setOpened(true)}>
            <svg viewBox="0 0 1024 1024" className="w-full h-full">
               <defs>
                  <linearGradient x1="50%" y1="92.034%" x2="50%" y2="7.2%" id="a">
                     <stop offset="0%" stopColor="#a5b4fc" />
                     <stop offset="100%" stopColor="#67e8f9" />
                  </linearGradient>
               </defs>
               <path fill="url(#a)" d="M761.1 288.3L687.8 215 325.1 577.6l-15.6 89 88.9-15.7z"></path>
               <path
                  fill="url(#a)"
                  d="M880 836H144c-17.7 0-32 14.3-32 32v36c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-36c0-17.7-14.3-32-32-32zm-622.3-84c2 0 4-.2 6-.5L431.9 722c2-.4 3.9-1.3 5.3-2.8l423.9-423.9a9.96 9.96 0 0 0 0-14.1L694.9 114.9c-1.9-1.9-4.4-2.9-7.1-2.9s-5.2 1-7.1 2.9L256.8 538.8c-1.5 1.5-2.4 3.3-2.8 5.3l-29.5 168.2a33.5 33.5 0 0 0 9.4 29.8c6.6 6.4 14.9 9.9 23.8 9.9zm67.4-174.4L687.8 215l73.3 73.3-362.7 362.6-88.9 15.7 15.6-89z"
               ></path>
            </svg>
         </button>
         <Modal
            opened={opened}
            onClose={async () => {
               setOpened(false)
               const diffObj = diff(initProfile, profile)
               if (uploadBlob) {
                  const b64 = fromUint8Array(new Uint8Array(await uploadBlob.arrayBuffer()), false)

                  diffObj['avatar'] =
                     `/api/restful/file/${initProfile.userId}/avatar/` +
                     (
                        await axios.post(
                           `${window.location.origin}/api/restful/file/${
                              initProfile.userId
                           }/avatar/${encodeURIComponent(uploadBlob.type)}`,
                           {
                              payload: b64,
                           },
                        )
                     ).data['data']['fileName']
               }

               if (Object.keys(diffObj).length !== 0) {
                  if (diffObj['interests']) diffObj['interests'] = profile['interests']

                  const newProfile: UserProfile = (
                     await axios.put(`${window.location.origin}/api/restful/profile`, {
                        ...diffObj,
                        userId: profile.userId,
                     })
                  ).data['data']
                  newProfile.photos = initProfile.photos

                  onEditedSuccess(newProfile)
               }
            }}
            title="Edit profile"
            size="45rem"
            classNames={{
               title: 'text-2xl text-center w-full ml-4',
            }}
         >
            <div className="flex flex-col items-center">
               <div className="flex w-full">
                  <div className="text-xl font-bold">Profile Picture</div>
                  <div className="flex-grow"></div>
                  <button
                     className="p-2 text-blue-500 rounded-md hover:bg-slate-200/50"
                     onClick={() => setAvatarModalOpened(true)}
                  >
                     Edit
                  </button>

                  <Modal
                     opened={avatarModalOpened}
                     centered
                     onClose={() => setAvatarModalOpened(false)}
                     title="Edit avatar"
                     size="30rem"
                  >
                     <div className="flex flex-col items-center w-full p-6 gap-y-5">
                        <BlobImage file={uploadBlob ? uploadBlob : profile.avatar} />
                        <input
                           type="file"
                           accept="image/png, image/jpeg"
                           className="hidden"
                           ref={fileInputRef}
                           onChange={(e) => {
                              setUploadBlob(e.target.files[0])
                           }}
                        />
                        <button
                           className="w-full h-10 text-blue-600 bg-blue-200 rounded-md"
                           onClick={() => fileInputRef.current?.click()}
                        >
                           Upload image
                        </button>
                     </div>
                  </Modal>
               </div>
               <BlobImage file={uploadBlob ? uploadBlob : profile.avatar} />
               <div className="flex w-full">
                  <div className="text-xl font-bold">Bio</div>
                  <div className="flex-grow"></div>
                  <div className="flex-grow"></div>
                  <button
                     className="p-2 text-blue-500 rounded-md hover:bg-slate-200/50"
                     onClick={() => setBioModalOpened(true)}
                  >
                     Edit
                  </button>
                  <Modal
                     opened={bioModalOpened}
                     centered
                     onClose={() => setBioModalOpened(false)}
                     title="Edit bio"
                     size="35rem"
                  >
                     <textarea
                        className="w-full h-full p-3 border-2 rounded-md focus:outline-none border-slate-300 text-slate-700"
                        value={profile.bio}
                        onChange={(e) =>
                           setProfile((c) => {
                              return {
                                 ...c,
                                 bio: e.target.value,
                              }
                           })
                        }
                        rows={10}
                     ></textarea>
                  </Modal>
               </div>
               <div className="w-full p-6 text-lg text-center text-slate-600">{profile.bio}</div>
               <div className="flex w-full">
                  <div className="text-xl font-bold">Interests</div>
                  <div className="flex-grow"></div>
                  <button
                     className="p-2 text-blue-500 rounded-md hover:bg-slate-200/50"
                     onClick={() => setChipsModalOpened(true)}
                  >
                     Edit
                  </button>
                  <ChipsModal
                     allChips={defaultInterests}
                     chips={chips}
                     setChips={setChips}
                     opened={chipsModalOpened}
                     setOpened={setChipsModalOpened}
                  />
               </div>
               <ChipsInProfile values={chips} />
               <div className="flex w-full">
                  <div className="text-xl font-bold">Infos</div>
                  <div className="flex-grow"></div>
                  <button
                     className="p-2 text-blue-500 rounded-md hover:bg-slate-200/50"
                     onClick={() => setInfosModalOpened(true)}
                  >
                     Edit
                  </button>
                  <Modal
                     opened={infosModalOpened}
                     centered
                     onClose={() => setInfosModalOpened(false)}
                     title="Edit infos"
                     size="30rem"
                  >
                     <div className="flex flex-col w-full p-6 gap-y-2">
                        <div className="flex items-center w-full gap-x-8">
                           <FaUser className="w-8 h-8 text-blue-600" />
                           <input
                              type="text"
                              className="w-full py-2 border-b-2 border-slate-500 focus:border-blue-500 text-slate-700 focus:outline-none"
                              value={profile.name}
                              onChange={(e) =>
                                 setProfile((c) => {
                                    return {
                                       ...c,
                                       name: e.target.value,
                                    }
                                 })
                              }
                           />
                        </div>
                        <div className="flex items-center gap-x-8">
                           <FaBirthdayCake className="w-8 h-8 text-blue-600" />
                           <DatePicker
                              placeholder="Pick date"
                              inputFormat="MM/DD/YYYY"
                              value={new Date(profile.birthday)}
                              onChange={(e) =>
                                 setProfile((c) => {
                                    return {
                                       ...c,
                                       birthday: e.getTime(),
                                    }
                                 })
                              }
                              radius="md"
                              size="md"
                              classNames={{
                                 root: 'border-b-2 border-slate-500 w-full',
                              }}
                              icon={
                                 <svg
                                    className="w-5 h-5 text-gray-500 dark:text-gray-400"
                                    fill="currentColor"
                                    viewBox="0 0 20 20"
                                 >
                                    <path
                                       fillRule="evenodd"
                                       d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z"
                                       clipRule="evenodd"
                                    ></path>
                                 </svg>
                              }
                           />
                        </div>
                        <div className="flex items-center gap-x-8">
                           <MdLocationOn className="w-8 h-8 text-blue-600 transform scale-125" />
                           <input
                              type="text"
                              className="w-full py-2 border-b-2 border-slate-500 focus:border-blue-500 text-slate-700 focus:outline-none"
                              value={profile.address}
                              onChange={(e) =>
                                 setProfile((c) => {
                                    return {
                                       ...c,
                                       address: e.target.value,
                                    }
                                 })
                              }
                           />
                        </div>
                        <div className="flex items-center gap-x-8">
                           <AiFillMobile className="text-blue-600 transform scale-125 w-7 h-7" />{' '}
                           <input
                              type="text"
                              className="w-full py-2 border-b-2 border-slate-500 focus:border-blue-500 text-slate-700 focus:outline-none"
                              value={profile.phone}
                              onChange={(e) =>
                                 setProfile((c) => {
                                    return {
                                       ...c,
                                       phone: e.target.value,
                                    }
                                 })
                              }
                           />
                        </div>
                     </div>
                  </Modal>
               </div>
               <div className="flex flex-col w-full p-6 gap-y-2">
                  <div className="flex items-center gap-x-8">
                     <FaUser className="w-8 h-8 text-blue-600" />
                     <span className="text-slate-700">{profile.name}</span>
                  </div>
                  <div className="flex items-center gap-x-8">
                     <FaBirthdayCake className="w-8 h-8 text-blue-600" />
                     <span className="text-slate-700">{new Date(profile.birthday).toLocaleDateString()}</span>
                  </div>
                  {profile.address && (
                     <div className="flex items-center gap-x-8">
                        <MdLocationOn className="w-8 h-8 text-blue-600" />
                        <span className="text-slate-700">{profile.address}</span>
                     </div>
                  )}
                  {profile.phone && (
                     <div className="flex items-center gap-x-8">
                        <AiFillMobile className="w-8 h-8 p-0.5 text-blue-600" />
                        <span className="text-slate-700">{profile.phone}</span>
                     </div>
                  )}
               </div>
            </div>
         </Modal>
      </>
   )
}

export default EditProfile
