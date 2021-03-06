import { Image, Popover } from '@mantine/core'
import classNames from 'classnames'
import { useState } from 'react'
import { RiEmotionLine } from 'react-icons/ri'
import { Socket } from 'socket.io-client'

function ReactSelect({ message, socket, init }: { message: Message; socket: Socket; init: boolean }) {
   const [opened, setOpened] = useState(false)

   const reactToMessage = (react: React) => {
      if (!init) return

      socket.emit('message:react', {
         messageId: message.messageId,
         react: react === message.react ? 'none' : react,
      })
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
               {Object.values(['love', 'haha', 'wow', 'sad', 'angry']).map((emotion: React, i: number) => {
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
                           radius={100}
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
