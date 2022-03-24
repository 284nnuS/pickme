import { Modal } from '@mantine/core'
import classNames from 'classnames'
import { useEffect, useState } from 'react'
import { AiFillMessage, AiFillWarning } from 'react-icons/ai'
import { BsFillFlagFill } from 'react-icons/bs'
import { GiRobberMask } from 'react-icons/gi'
import { IoMdArrowBack } from 'react-icons/io'
import { MdOutlinePermMedia } from 'react-icons/md'
import { RiErrorWarningFill } from 'react-icons/ri'
import { io } from 'socket.io-client'

function ReportButton({ reported, inCard }: { reported: number; inCard: boolean }) {
   const [opened, setOpened] = useState(false)
   const [section, setSection] = useState('tag')
   const [report, setReport] = useState<Report>({ reported } as Report)
   const [submitted, setSubmitted] = useState(false)

   const socket = io('/report', {
      timeout: 5000,
      transports: ['websocket'],
      upgrade: false,
   })
   const [init, setInit] = useState(false)

   useEffect(() => {
      socket
         .open()
         .on('connect', () => setInit(true))
         .on('disconnect', () => {
            socket.removeAllListeners()
         })

      return () => {
         socket.disconnect()
      }
   }, [])

   const setReportTag = (tag: string) => {
      setReport((el) => {
         return {
            ...el,
            tag,
         }
      })
      setSection('additionalInfo')
   }

   const handleSubmit = (e) => {
      e.preventDefault()
      if (!init) return
      socket.emit('report:request', report)
      setOpened(false)
      setTimeout(() => setSubmitted(true), 200)
   }

   return (
      <>
         <button
            className={classNames('w-10 h-10 p-2 rounded-full hover:bg-slate-300/60 ', inCard && 'bg-slate-400')}
            onClick={() => setOpened(true)}
         >
            <BsFillFlagFill className={classNames('w-full h-full', inCard ? 'text-white' : 'text-red-700')} />
         </button>
         <Modal
            opened={opened}
            onClose={() => setOpened(false)}
            centered
            title={
               <>
                  {!submitted && section !== 'tag' && (
                     <div className="absolute top-0 bottom-0 left-0 right-auto flex flex-col justify-center ">
                        <button className="w-12 h-12 rounded-full" onClick={() => setSection('tag')}>
                           <IoMdArrowBack className="w-full h-full text-red-500 rounded-full hover:text-white hover:bg-red-500" />
                        </button>
                     </div>
                  )}
                  <RiErrorWarningFill className="w-20 h-20 text-red-500" />
               </>
            }
            classNames={{
               title: 'ml-4 flex w-full items-center relative justify-center',
               close: 'hidden',
            }}
         >
            {submitted ? (
               <div className="text-xl font-bold text-center text-slate-700">You already have reported this user</div>
            ) : (
               <form className="flex flex-col items-center" onSubmit={handleSubmit}>
                  <div className="text-3xl font-bold text-slate-700">Report User</div>
                  {section === 'tag' && (
                     <div className="flex flex-col py-6">
                        {inCard || (
                           <button
                              type="button"
                              className="flex items-center px-6 py-2 rounded-full gap-x-6 hover:bg-slate-200"
                              onClick={() => setReportTag('inappropriate messages')}
                           >
                              <AiFillMessage className="w-10 h-10 text-blue-500" />
                              <span className="text-lg font-semibold text-slate-400">Inappropriate messages</span>
                           </button>
                        )}
                        <button
                           type="button"
                           className="flex items-center px-6 py-2 rounded-full gap-x-6 hover:bg-slate-200"
                           onClick={() => setReportTag('inappropriate photo/voice')}
                        >
                           <MdOutlinePermMedia className="w-10 h-10 text-indigo-500" />
                           <span className="text-lg font-semibold text-slate-400">Inappropriate photo/voice</span>
                        </button>
                        {inCard || (
                           <button
                              type="button"
                              className="flex items-center px-6 py-2 rounded-full gap-x-6 hover:bg-slate-200"
                              onClick={() => setReportTag('scam')}
                           >
                              <GiRobberMask className="w-10 h-10 text-yellow-500" />
                              <span className="text-lg font-semibold text-slate-400">Scam</span>
                           </button>
                        )}
                        <button
                           type="button"
                           className="flex items-center px-6 py-2 rounded-full gap-x-6 hover:bg-slate-200"
                           onClick={() => setReportTag('other')}
                        >
                           <AiFillWarning className="w-10 h-10 text-green-500" />
                           <span className="text-lg font-semibold text-slate-400">Other</span>
                        </button>
                     </div>
                  )}
                  {section === 'additionalInfo' && (
                     <div className="flex flex-col items-center w-full px-5 py-6 gap-y-5">
                        <div className="text-lg font-semibold text-slate-500">Please let us know about situations</div>
                        <textarea
                           required={report.tag === 'other'}
                           onChange={(e) =>
                              setReport((el) => {
                                 return {
                                    ...el,
                                    additionalInfo: e.target.value,
                                 }
                              })
                           }
                           className="w-full p-5 rounded-xl focus:outline-none bg-slate-100"
                           rows={6}
                        />
                        <button
                           type="submit"
                           className="w-32 h-10 text-lg font-semibold text-red-600 border-2 border-red-600 rounded-md bg-red-50 hover:text-white hover:bg-red-600"
                        >
                           Submit
                        </button>
                     </div>
                  )}
               </form>
            )}
         </Modal>
      </>
   )
}

export default ReportButton
