import { Modal, Popover } from '@mantine/core'
import axios from 'axios'
import { IEmojiPickerProps } from 'emoji-picker-react'
import dynamic from 'next/dynamic'
import { useState } from 'react'
import { AiTwotoneEdit } from 'react-icons/ai'

const EmojiPickerNoSSRWrapper = dynamic<IEmojiPickerProps>(() => import('emoji-picker-react'), {
   ssr: false,
   loading: () => <p>Loading ...</p>,
})

function ProfileStatus({
   editable = false,
   userId,
   initStatusEmoji,
   initStatusText,
   onEditedSuccess,
}: {
   editable: boolean
   userId: number
   initStatusEmoji: string
   initStatusText: string
   onEditedSuccess: (emoji: string, text: string) => void
}) {
   const [opened, setOpened] = useState(false)
   const [openedPicker, setOpenedPicker] = useState(false)

   const [statusEmoji, setStatusEmoji] = useState(initStatusEmoji)
   const [statusText, setStatusText] = useState(initStatusText)

   if (!editable && !statusEmoji) return <></>

   return (
      <div className="flex items-center text-lg text-slate-500">
         <div className="relative px-8 group">
            {statusEmoji || 'No status'}
            {!!statusText && <span className="ml-3">{statusText}</span>}
            {editable && (
               <button
                  className="absolute top-0 bottom-0 right-0 hidden w-6 h-6 ml-6 rounded-full group-hover:block"
                  onClick={() => setOpened(true)}
               >
                  <AiTwotoneEdit className="w-full h-full text-slate-500" />
               </button>
            )}
            <Modal
               opened={opened}
               centered
               onClose={async () => {
                  try {
                     await axios.put(`${window.location.origin}/api/restful/profile`, {
                        statusText,
                        statusEmoji,
                        userId,
                     })

                     onEditedSuccess(statusEmoji, statusText)
                  } catch {
                     setStatusEmoji(initStatusEmoji)
                     setStatusText(initStatusText)
                  }
                  setOpened(false)
               }}
               title="What happens?"
            >
               <div className="flex items-center justify-center gap-x-3">
                  <Popover
                     opened={openedPicker}
                     onClose={() => setOpenedPicker(false)}
                     target={
                        <button
                           className="w-10 h-10 text-2xl leading-10 text-center border rounded-full"
                           onClick={() => setOpenedPicker((c) => !c)}
                        >
                           {statusEmoji}
                        </button>
                     }
                     position="top"
                     spacing={0}
                     gutter={20}
                     withArrow
                  >
                     <EmojiPickerNoSSRWrapper
                        onEmojiClick={(_, emojiObject) => {
                           setStatusEmoji(emojiObject.emoji)
                           setOpenedPicker(false)
                        }}
                     />
                  </Popover>
                  <input
                     type="text"
                     value={statusText}
                     onChange={(e) => setStatusText(e.target.value)}
                     className="h-10 px-3 border-b-2 border-slate-500 w-72 focus:outline-none focus:border-blue-500"
                  />
               </div>
            </Modal>
         </div>
      </div>
   )
}

export default ProfileStatus
