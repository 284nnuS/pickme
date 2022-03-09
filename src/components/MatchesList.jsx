import React, { useState } from 'react'
import { FaUserAlt } from 'react-icons/fa'
import { TiArrowBack } from 'react-icons/ti'
import classNames from 'classnames'
import MatchesForm from './MatchesForm'
import MessageForm from './MessageForm'

function MatchesList(matches, messages) {
   matches = [
      {
         id: 1,
         avt: 'https://scontent.fdad2-1.fna.fbcdn.net/v/t1.6435-9/134816387_1376857929329964_4258482202820745964_n.jpg?_nc_cat=107&ccb=1-5&_nc_sid=8bfeb9&_nc_ohc=2KVu4WdgjfUAX_WZHqX&tn=qdKq5EqeZYz1gFTW&_nc_ht=scontent.fdad2-1.fna&oh=00_AT-vS1t9gtpbW_NrS_LXgZVKrnBFHrmNfACkBJuOdvRtkA&oe=624AD716',
         name: 'Dung',
      },
      {
         id: 2,
         avt: 'https://scontent.fdad2-1.fna.fbcdn.net/v/t1.6435-9/133807166_1370288076653616_1821247264078180559_n.jpg?_nc_cat=100&ccb=1-5&_nc_sid=174925&_nc_ohc=RbeJsQ9IBMQAX8vTxVj&tn=qdKq5EqeZYz1gFTW&_nc_ht=scontent.fdad2-1.fna&oh=00_AT8ipUIapqv76yPq2ohdeOkXk4ib0PVmc3mt-LJjcuBL1A&oe=624CE545',
         name: 'Bone',
      },
      {
         id: 3,
         avt: 'https://scontent.fdad1-2.fna.fbcdn.net/v/t1.6435-9/109946554_1232222130460212_2377571917506715282_n.jpg?_nc_cat=106&ccb=1-5&_nc_sid=174925&_nc_ohc=Hk6TLhF_Xx4AX_mQgqM&_nc_ht=scontent.fdad1-2.fna&oh=00_AT9KWq65We9hgFeUOybT_S0TVHHfLv9lRfdq4fVHo5eltA&oe=624A9414',
         name: 'Xuong',
      },
   ]

   messages = [
      {
         id: 1,
         avt: 'https://scontent.fdad2-1.fna.fbcdn.net/v/t1.6435-9/134816387_1376857929329964_4258482202820745964_n.jpg?_nc_cat=107&ccb=1-5&_nc_sid=8bfeb9&_nc_ohc=2KVu4WdgjfUAX_WZHqX&tn=qdKq5EqeZYz1gFTW&_nc_ht=scontent.fdad2-1.fna&oh=00_AT-vS1t9gtpbW_NrS_LXgZVKrnBFHrmNfACkBJuOdvRtkA&oe=624AD716',
         name: 'Dung',
         content: 'Em nho anh nhieu lam',
         isSend: true,
      },
      {
         id: 2,
         avt: 'https://scontent.fdad2-1.fna.fbcdn.net/v/t1.6435-9/133807166_1370288076653616_1821247264078180559_n.jpg?_nc_cat=100&ccb=1-5&_nc_sid=174925&_nc_ohc=RbeJsQ9IBMQAX8vTxVj&tn=qdKq5EqeZYz1gFTW&_nc_ht=scontent.fdad2-1.fna&oh=00_AT8ipUIapqv76yPq2ohdeOkXk4ib0PVmc3mt-LJjcuBL1A&oe=624CE545',
         name: 'Bone',
         content: 'Em khong nho anh nhieu lam',
         isSend: false,
      },
      {
         id: 3,
         avt: 'https://scontent.fdad1-2.fna.fbcdn.net/v/t1.6435-9/109946554_1232222130460212_2377571917506715282_n.jpg?_nc_cat=106&ccb=1-5&_nc_sid=174925&_nc_ohc=Hk6TLhF_Xx4AX_mQgqM&_nc_ht=scontent.fdad1-2.fna&oh=00_AT9KWq65We9hgFeUOybT_S0TVHHfLv9lRfdq4fVHo5eltA&oe=624A9414',
         name: 'Xuong',
         content: 'Em khong nho anh nhieu lam',
         isSend: false,
      },
   ]

   const [onMatch, setOnMatch] = useState(true)
   const [onMess, setOnMess] = useState(false)

   const handleMatches = () => {
      setOnMatch(true)
      setOnMess(false)
   }

   const handleMessages = () => {
      setOnMatch(false)
      setOnMess(true)
   }

   return (
      <div className="w-[30rem] h-screen">
         <div className="w-full h-[6rem] bg-gradient-to-r from-[#2f494d] to-[#68bdc4] flex justify-between">
            <div className="flex justify-around mt-5 ml-5">
               <div
                  style={{
                     backgroundImage: `url(https://scontent.fdad1-2.fna.fbcdn.net/v/t1.6435-9/137557375_2916092331943656_2214375182347264348_n.jpg?_nc_cat=105&ccb=1-5&_nc_sid=09cbfe&_nc_ohc=1ES32iLSZlwAX_5QvzO&_nc_ht=scontent.fdad1-2.fna&oh=00_AT-frP35glMvQdDra8K58Dh6XP41huRg46RAVAuaZJhokg&oe=6243B3C9)`,
                  }}
                  className="w-14 h-14 rounded-full bg-cover bg-center"
               ></div>
               <a href="#">
                  <div className="text-white font-medium text-2xl mt-2 ml-2">Le Trung Dung</div>
               </a>
            </div>

            <div className="mt-5 mr-5">
               <button>
                  <div className="w-14 h-14 rounded-full grid grid-cols-1 bg-white place-items-center">
                     <div>
                        <FaUserAlt className="w-6 h-6 text-[#2f494d]" />
                     </div>
                  </div>
               </button>
            </div>
         </div>

         <div className="w-full h-[90%] bg-slate-100">
            <div className="flex justify-around pt-3 pb-1">
               <button
                  type="button"
                  className={classNames(
                     'text-black font-semibold text-xl border-b-4 border-b-slate-100',
                     onMatch && 'border-b-4 border-b-black',
                  )}
                  onClick={handleMatches}
               >
                  Matches
               </button>
               <button
                  type="button"
                  className={classNames(
                     'text-black font-semibold text-xl border-b-4 border-b-slate-100',
                     onMess && 'border-b-4 border-b-black',
                  )}
                  onClick={handleMessages}
               >
                  Messages
               </button>
            </div>

            {onMatch && (
               <div className="grid grid-cols-3 pt-3 gap-x-1 gap-y-5 ml-5">
                  {matches.map((match) => (
                     <MatchesForm key={match.id} {...match} />
                  ))}
               </div>
            )}

            {onMess && (
               <div className="grid grid-cols-1 pt-3">
                  {messages.map((message) => (
                     <MessageForm key={message.id} {...message} />
                  ))}
               </div>
            )}
         </div>
      </div>
   )
}

export default MatchesList
