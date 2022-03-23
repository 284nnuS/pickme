import { Avatar, Divider } from '@mantine/core'
import Link from 'next/link'
import { AiFillCheckCircle, AiFillCloseCircle } from 'react-icons/ai'
import { Socket } from 'socket.io-client'
import { getDiffTimeToString } from 'src/utils/time'

function ReportRequestItem({ report, socket, init }: { report: Report; socket: Socket; init: boolean }) {
   return (
      <div className="flex flex-col w-full p-8 rounded-lg gap-y-6 bg-slate-100">
         <div className="flex items-center w-full gap-x-8">
            <Avatar src={report.reportedProfile.avatar} alt={report.reportedProfile.name} size={96} radius={100} />
            <div className="flex flex-col text-2xl font-bold">
               <Link href={`/app/profile/${report.reported}`} passHref>
                  <a>{report.reportedProfile.name}</a>
               </Link>
               <div className="text-sm font-semibold text-slate-500">{getDiffTimeToString(report.time)}</div>
               <div className="text-sm font-normal text-slate-500">
                  {(() => {
                     if (report.reportedUserInfo.cautionTimes === 0) return "Haven't violated any time before"
                     if (report.reportedUserInfo.cautionTimes === 1) return 'Violated 1 time'
                     return `Violated ${report.reportedUserInfo.cautionTimes} times`
                  })()}
               </div>
            </div>
            <div className="px-6 py-0.5 text-center text-sm text-white uppercase bg-red-500 rounded-full">
               {report.tag}
            </div>
            <div className="flex flex-grow"></div>
            {report.resolved === 'approve' && (
               <div className="flex items-center px-3 py-1 text-sm font-semibold uppercase rounded-full gap-x-1 text-emerald-500 bg-emerald-100">
                  <AiFillCheckCircle className="w-6 h-6 text-emerald-500" />
                  {report.resolved}
               </div>
            )}
            {report.resolved === 'decline' && (
               <div className="flex items-center px-3 py-1 text-sm font-semibold text-red-500 uppercase bg-red-100 rounded-full gap-x-1">
                  <AiFillCloseCircle className="w-6 h-6 text-red-500" />
                  {report.resolved}
               </div>
            )}
            {report.resolved === 'none' && (
               <div className="flex items-center gap-x-10">
                  <button
                     className="w-32 h-10 text-lg font-semibold text-white bg-red-700 rounded-md hover:bg-red-500 focus:outline-none"
                     onClick={() =>
                        init &&
                        socket.emit('report:resolve', {
                           reportId: report.reportId,
                           action: 'ban',
                        })
                     }
                  >
                     Ban
                  </button>
                  <button
                     className="w-32 h-10 text-lg font-semibold text-white bg-yellow-700 rounded-md hover:bg-yellow-500 focus:outline-none"
                     onClick={() =>
                        init &&
                        socket.emit('report:resolve', {
                           reportId: report.reportId,
                           action: 'warn',
                        })
                     }
                  >
                     Warn
                  </button>
                  <button
                     className="w-32 h-10 text-lg font-semibold border-2 rounded-md text-slate-500 border-slate-500 hover:bg-slate-500 hover:text-white focus:outline-none"
                     onClick={() =>
                        init &&
                        socket.emit('report:resolve', {
                           reportId: report.reportId,
                           action: 'decline',
                        })
                     }
                  >
                     Decline
                  </button>
               </div>
            )}
         </div>
         <Divider />
         <div className="flex items-center gap-x-12">
            <div className="w-32 text-lg font-semibold">Additional Info</div>
            <p className="text-slate-500">{report.additionalInfo ? report.additionalInfo : 'None'}</p>
         </div>
         <Divider />
         <div className="text-lg text-slate-500">
            Reported by
            <Link href={`/app/profile/${report.reporter}`} passHref>
               <a className="ml-3 underline">{report.reporterProfile.name}</a>
            </Link>
         </div>
      </div>
   )
}

export default ReportRequestItem
