import { DatePicker } from '@mantine/dates'
import { useState } from 'react'
import { BsPlus } from 'react-icons/bs'
import { Chips, ChipsModal, SegmentedControl, PhotoUpload, VoiceUpload } from '~/src/components'
import axios from 'axios'
import env from '~/shared/env'
import { JWT } from 'next-auth/jwt'
import { useNotifications } from '@mantine/notifications'
import { useRouter } from 'next/router'

export function SignUp({ allInterests, name }) {
   const [fullName, setFullName] = useState(name)
   const [gender, setGender] = useState(null)
   const [birthday, setBirthday] = useState(new Date())
   const [biography, setBiography] = useState('')
   const [photos, setPhotos] = useState<string[]>([])
   const [voice, setVoice] = useState<FileInfo>({ name: '', dataUrl: null })
   const [interests, setInterests] = useState<InterestChip[]>([])

   const router = useRouter()

   const [opened, setOpened] = useState(false)

   const reg = /^data:(.+);base64,(.*)$/

   const notifications = useNotifications()

   const convert = (el: string) => {
      const matches = el.match(reg)
      return {
         type: matches[1],
         payload: matches[2],
      }
   }

   const submit = (e) => {
      e.preventDefault()

      if (!gender) {
         notifications.showNotification({
            title: 'Error',
            message: 'Please select your gender',
            color: 'red',
         })
         e.preventDefault()
         return
      }

      if (photos.length < 2) {
         notifications.showNotification({
            title: 'Error',
            message: 'Please add at least two photos to continue',
            color: 'red',
         })
         e.preventDefault()
         return
      }

      if (!voice.dataUrl || voice.dataUrl.match(reg).length !== 3) {
         notifications.showNotification({
            title: 'Error',
            message: 'Please add your voice to continue',
            color: 'red',
         })
         return
      }

      if (interests.length === 0) {
         notifications.showNotification({
            title: 'Error',
            message: 'Please add at least one interest to continue',
            color: 'red',
         })
         return
      }

      const data = {
         name: fullName,
         birthday: birthday.getTime(),
         gender,
         bio: biography,
         interests: interests.map((el) => el.name),
      }

      axios.post('/api/signUp', data).then(async (el) => {
         const userId = el.data['data']

         await Promise.all([
            ...photos.map(async (el) => {
               const obj = convert(el)

               await axios.post(
                  `${window.location.origin}/api/restful/file/${userId}/photo/${encodeURIComponent(obj.type)}`,
                  {
                     payload: obj.payload,
                  },
               )
            }),
         ])

         const obj = convert(voice.dataUrl)

         await axios.post(
            `${window.location.origin}/api/restful/file/${userId}/voice/${encodeURIComponent(obj.type)}`,
            {
               payload: obj.payload,
            },
         )
         router.replace(`${window.location.origin}/app`)
      })
   }

   return (
      <div className="flex items-center justify-center w-screen min-h-screen">
         <form className="flex flex-col items-center w-full px-6 py-10 md:w-auto" onSubmit={submit}>
            <h1 className="mb-6 text-3xl font-bold text-center md:mb-16">CREATE ACCOUNT</h1>
            <div className="flex flex-col md:flex-row gap-x-10 gap-y-5 w-full md:w-[55rem]">
               <div className="flex flex-col md:w-1/2 gap-y-5">
                  <div>
                     <label
                        htmlFor="email-adress-icon"
                        className="block text-sm font-bold text-gray-900 dark:text-gray-300"
                     >
                        Name
                     </label>
                     <div className="relative mt-2">
                        <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                           <svg
                              className="w-5 h-5 text-gray-500 dark:text-gray-400"
                              fill="currentColor"
                              viewBox="0 0 20 20"
                           >
                              <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"></path>
                              <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"></path>
                           </svg>
                        </div>
                        <input
                           type="text"
                           id="email-adress-icon"
                           value={fullName}
                           onChange={(e) => setFullName(e.target.value)}
                           className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:border-blue-500 focus:outline-none block w-full pl-10 p-2.5"
                           placeholder="Enter your name"
                           required
                        />
                     </div>
                  </div>
                  <SegmentedControl value={gender} setValue={setGender} data={['male', 'female', 'others']} />
                  <div>
                     <label className="block mb-2 text-sm font-bold text-gray-900 dark:text-gray-400">Birthday</label>
                     <DatePicker
                        placeholder="Pick date"
                        inputFormat="MM/DD/YYYY"
                        value={birthday}
                        onChange={setBirthday}
                        radius="md"
                        size="md"
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
                  <div>
                     <label className="block mb-2 text-sm font-bold text-gray-900 dark:text-gray-400">Biography</label>
                     <textarea
                        id="message"
                        rows={8}
                        value={biography}
                        onChange={(e) => setBiography(e.target.value)}
                        className="block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 focus:outline-none dark:text-white ring-0"
                        placeholder="Write your biography here."
                        required
                     ></textarea>
                  </div>
               </div>
               <div className="flex flex-col md:w-1/2 gap-y-5">
                  <PhotoUpload photos={photos} setPhotos={setPhotos} />
                  <div className="mt-1 mb-3 text-sm text-center text-gray-500 dark:text-gray-300" id="user_avatar_help">
                     Add at least 2 photos to continue
                  </div>
                  <VoiceUpload voice={voice} setVoice={setVoice} />
                  <div className="flex flex-col gap-y-2">
                     <label className="block text-sm font-bold text-gray-900 dark:text-gray-300" htmlFor="user_avatar">
                        Interest
                     </label>
                     <Chips values={interests} setValues={setInterests} />
                     <button
                        type="button"
                        className="w-8 h-8 ml-0 border-none rounded-full bg-slate-100"
                        onClick={() => setOpened(true)}
                     >
                        <BsPlus className="m-auto w-7 h7" />
                     </button>
                     <ChipsModal
                        allChips={allInterests}
                        chips={interests}
                        setChips={setInterests}
                        opened={opened}
                        setOpened={setOpened}
                     />
                  </div>
               </div>
            </div>
            <button
               type="submit"
               className="w-24 h-10 mt-6 text-sm text-center text-white bg-gray-700 rounded-full hover:bg-gray-800 focus:ring-4 focus:ring-gray-300"
            >
               Continue
            </button>
         </form>
      </div>
   )
}

export async function getServerSideProps({ res }) {
   const { locals } = res
   const token: JWT = locals['token']

   return {
      props: {
         name: token.name,
         allInterests: (await axios.get(`${env.javaServerUrl}/interest`)).data['data'].map((el) => {
            return {
               name: el.interestName,
               description: el.description,
            }
         }),
      },
   }
}

export default SignUp
