import { Image } from '@mantine/core'
import classNames from 'classnames'
import Link from 'next/link'
import React, { useRef } from 'react'
import { TiArrowBack } from 'react-icons/ti'
import { getDiffTimeToString } from '~/src/utils/time'

const MessageForm = ({ item, selected }: { item: MessageItem; selected: boolean }) => {
   const { userId, messageId, time, avatar, name, isSender, content } = item

   const linkToOtherConversationRef = useRef<HTMLAnchorElement>()

   return (
      <button
         type="button"
         className={classNames(
            'flex items-center justify-start w-full px-6 py-3 gap-x-6 hover:rounded-2xl hover:bg-slate-300 focus:rounded-2xl',
            selected && 'rounded-2xl bg-slate-300',
         )}
         onClick={() => selected || linkToOtherConversationRef.current?.click()}
      >
         <Image src={avatar} alt={name} radius={100} width={60} height={60} />
         <div className="flex flex-col">
            <div className="text-xl font-semibold text-left">{name}</div>
            <div className="flex justify-start">
               {isSender && (
                  <div>
                     <TiArrowBack className="mr-2 w-7 h-7" />
                  </div>
               )}
               <div className={classNames('font-normal text-left', messageId && !content && 'text-slate-700 italic')}>
                  {messageId
                     ? content
                        ? content
                        : `${isSender ? 'Your' : name.split(' ')[0] + "'s"} message is removed`
                     : ''}
                  {time && <span className="ml-3 not-italic font-semibold">{getDiffTimeToString(time)}</span>}
               </div>
            </div>
         </div>
         <div className="hidden">
            <Link href={`/app/chat/${userId}`} passHref>
               <a ref={linkToOtherConversationRef}>App</a>
            </Link>
         </div>
      </button>
   )
}

export default MessageForm
