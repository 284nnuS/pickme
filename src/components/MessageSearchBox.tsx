import { Image, Modal } from '@mantine/core'
import Fuse from 'fuse.js'
import { useState } from 'react'
import { FaSearch } from 'react-icons/fa'
import { TiArrowBack } from 'react-icons/ti'
import { getDiffTimeToString } from '../utils/time'

function MessageSearchBox({
   yourAvatar,
   conversation,
   messages,
   scrollToMessage,
}: {
   yourAvatar: string
   conversation: Conversation
   messages: Message[]
   scrollToMessage: (index: number) => void
}) {
   const [opened, setOpened] = useState(false)
   const [keywords, setKeywords] = useState('')

   const fuse = new Fuse(messages, {
      keys: ['content'],
   })

   const result = keywords.length >= 3 ? fuse.search(keywords) : null

   return (
      <div>
         <button className="w-8 h-8 p-1.5 bg-teal-500 rounded-full" onClick={() => setOpened(true)}>
            <FaSearch className="w-full h-full text-white bg-teal-500" />
         </button>
         <Modal centered opened={opened} onClose={() => setOpened(false)} title="Search message" overflow="inside">
            <input
               className="w-full px-6 py-3 transition-all border-b-2 border-transparent rounded-full outline-none focus:border-teal-500 bg-slate-200 focus:rounded-none focus:bg-transparent"
               type="text"
               placeholder="Type keywords (at latest 3 characters)"
               value={keywords}
               onChange={(e) => setKeywords(e.target.value)}
            />
            <ul className="flex flex-col mt-6">
               {result && result.length > 0
                  ? result.map((el) => {
                       return (
                          <li key={el.item.conversationId}>
                             <button
                                type="button"
                                onClick={() => {
                                   scrollToMessage(el.refIndex)
                                   setOpened(false)
                                }}
                                className="flex items-center justify-start w-full py-3 gap-x-6 hover:rounded-2xl hover:bg-slate-200 focus:rounded-2xl"
                             >
                                <Image
                                   src={el.item.sender === conversation.otherId ? conversation.otherAvatar : yourAvatar}
                                   alt=""
                                   radius={100}
                                   width={60}
                                   height={60}
                                />
                                <div className="flex flex-col">
                                   <div className="text-xl font-semibold text-left">
                                      {el.item.sender === conversation.otherId ? conversation.otherName : 'You'}
                                   </div>
                                   <div className="flex justify-start">
                                      {el.item.sender !== conversation.otherId && (
                                         <div>
                                            <TiArrowBack className="mr-2 w-7 h-7" />
                                         </div>
                                      )}
                                      <div className="font-normal text-left">
                                         {el.item.content}
                                         <span className="ml-3 not-italic font-semibold">
                                            {getDiffTimeToString(el.item.time)}
                                         </span>
                                      </div>
                                   </div>
                                </div>
                             </button>
                          </li>
                       )
                    })
                  : result && <div className="text-lg text-center text-bold text-slate-600">No messages found</div>}
            </ul>
         </Modal>
      </div>
   )
}

export default MessageSearchBox
