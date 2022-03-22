import { Image } from '@mantine/core'
import classNames from 'classnames'
import { useRouter } from 'next/router'
import React from 'react'
import { TiArrowBack } from 'react-icons/ti'
import { getDiffTimeToString } from '~/src/utils/time'

function ConversationItem({
   item,
   selected,
   scrollCallback,
}: {
   item: Conversation
   selected: boolean
   scrollCallback: () => void
}) {
   const { conversationId, otherName, otherAvatar, latestTime, latestMessage, sender } = item
   const router = useRouter()

   return (
      <button
         type="button"
         className={classNames(
            'flex items-center justify-start w-full px-6 py-3 gap-x-6 hover:rounded-2xl hover:bg-slate-300 focus:rounded-2xl',
            selected && 'rounded-2xl bg-slate-300',
         )}
         onClick={async () => {
            if (!selected) await router.push(`/app/chat/${conversationId}`)
            else scrollCallback()
         }}
      >
         <Image src={otherAvatar} alt={otherName} radius={100} width={60} height={60} />
         <div className="flex flex-col">
            <div className="text-xl font-semibold text-left">{otherName}</div>
            <div className="flex justify-start">
               {sender && (
                  <div>
                     <TiArrowBack className="mr-2 w-7 h-7" />
                  </div>
               )}
               <div
                  className={classNames(
                     'font-normal text-left',
                     latestTime && !latestMessage && 'text-slate-700 italic',
                  )}
               >
                  {latestTime
                     ? latestMessage
                        ? latestMessage
                        : `${sender ? 'Your' : otherName.split(' ')[0] + "'s"} message is removed`
                     : ''}
                  {latestTime && (
                     <span className="ml-3 not-italic font-semibold">{getDiffTimeToString(latestTime)}</span>
                  )}
               </div>
            </div>
         </div>
      </button>
   )
}

export default ConversationItem
