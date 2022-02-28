import { Popover } from '@mantine/core'
import classNames from 'classnames'
import Image from 'next/image'
import { useState } from 'react'
import { RiEmotionLine } from 'react-icons/ri'

function ReactSelect({ message, socket }) {
   const [opened, setOpened] = useState(false)

   const reactToMessage = (react) => {
      socket.emit('React to message', {
         messageId: message.messageId,
         sender: message.sender,
         receiver: message.receiver,
         react: react === message.react ? null : react,
      })
      console.log('Reacted')
      setOpened(false)
   }

   return (
      <div className="absolute top-0 bottom-0 items-center hidden group-hover:flex -right-0">
         <Popover
            opened={opened}
            onClose={() => setOpened(false)}
            position="top"
            placement="end"
            target={
               <button
                  className="relative p-1 bg-white rounded-full w-7 h-7 hover:bg-slate-300"
                  onClick={() => setOpened((current) => !current)}
               >
                  <RiEmotionLine className="w-full h-full text-gray-500" />
               </button>
            }
         >
            <div className="flex gap-x-2">
               {Object.values(['like', 'haha', 'wow', 'sad', 'angry']).map((emotion, i) => {
                  return (
                     <button
                        key={i}
                        onClick={() => reactToMessage(emotion)}
                        className={classNames(
                           'flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-200',
                           message.react === emotion && 'bg-gray-200',
                        )}
                     >
                        <Image
                           src={`/static/images/${emotion}32.png`}
                           alt={emotion}
                           layout="fixed"
                           width={32}
                           height={32}
                        />
                     </button>
                  )
               })}
            </div>
         </Popover>
      </div>
   )
}

export default ReactSelect
