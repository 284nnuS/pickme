import { Accordion, Avatar } from '@mantine/core'
import axios from 'axios'
import classNames from 'classnames'
import Head from 'next/head'
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { RiMoreFill } from 'react-icons/ri'
import { io } from 'socket.io-client'
import env from '~/shared/env'
import { AvatarMenu, ReportRequestItem } from '~/src/components'

function Reports({
   userProfile,
   initReportList,
   role,
}: {
   userProfile: UserProfile
   initReportList: Report[]
   role: string
}) {
   const router = useRouter()
   const display = router.query['display'] || 'unresolved'

   const [reportList, setReportList] = useState(initReportList)

   const socket = io('/report', {
      timeout: 5000,
      transports: ['websocket'],
      upgrade: false,
   })
   const [init, setInit] = useState(false)

   const process = (reports: Report[]) =>
      reports
         .filter((v: Report, i: number, a: Report[]) => a.findIndex((t) => t.reportId === v.reportId) === i)
         .sort((a, b) => b.time - a.time)

   useEffect(() => {
      socket.open()
      socket
         .on('report:new', (report: Report) => setReportList((c) => process([...c, report])))
         .on('report:update', (report: Report) =>
            setReportList((c) => {
               const index = c.findIndex((el) => el.reportId === report.reportId)
               c[index] = report
               return [...c]
            }),
         )
         .on('connect', () => setInit(true))
         .on('disconnect', () => {
            socket.removeAllListeners()
         })

      return () => {
         socket.disconnect()
      }
   }, [])

   const filtered = reportList.filter(
      (el) =>
         display === 'all' ||
         (el.resolved === 'none' && display === 'unresolved') ||
         (el.resolved !== 'none' && display === 'resolved'),
   )

   return (
      <>
         <Head>
            <title>Reports</title>
         </Head>
         <div className="flex overflow-hidden">
            <div className="w-[20rem] min-h-full bg-slate-100 flex-col p-6">
               <div className="flex items-center">
                  <Avatar src={userProfile.avatar} radius="xl" size="lg" alt={userProfile.name} />
                  <div className="ml-2 text-2xl font-bold text-slate-800">{userProfile.name}</div>
                  <div className="flex-grow"></div>
                  <AvatarMenu
                     profile={userProfile}
                     role={role}
                     control={
                        <button className="w-10 h-10 p-2 bg-white rounded-full">
                           <RiMoreFill className="w-full h-full text-teal-600" />
                        </button>
                     }
                  />
               </div>
               <div className="mt-6">
                  <Accordion
                     classNames={{
                        icon: 'hidden',
                        control: 'py-1',
                        label: 'font-bold text-slate-600 hover:bg-slate-200 px-3 py-1 rounded-r-full ',
                        item: 'border-none',
                        contentInner: 'p-0',
                     }}
                  >
                     <Accordion.Item label="Dashboard"></Accordion.Item>
                     <Accordion.Item label="Account Management"></Accordion.Item>
                     <Accordion.Item label="Report">
                        <div className="flex flex-col">
                           {Object.values(['unresolved', 'resolved', 'all']).map((el) => {
                              return (
                                 <button
                                    key={el}
                                    onClick={() => router.replace(`/manage/reports/?display=${el}`)}
                                    className={classNames(
                                       'border-l-[0.3rem] capitalize px-3 py-1 w-full font-bold rounded-r-full text-left',
                                       display === el
                                          ? 'border-emerald-500 bg-emerald-100 text-emerald-600'
                                          : 'border-transparent text-slate-600 hover:bg-slate-200',
                                    )}
                                 >
                                    {el}
                                 </button>
                              )
                           })}
                        </div>
                     </Accordion.Item>{' '}
                     <Accordion.Item label="Back" onClick={() => router.back()}></Accordion.Item>
                  </Accordion>
               </div>
            </div>
            <div className="relative flex flex-col flex-grow p-10 gap-y-8">
               <div className="text-3xl font-bold">Reports</div>
               <div className="flex flex-col w-full overflow-y-auto gap-y-3">
                  {filtered.length > 0 ? (
                     filtered.map((report) => {
                        return <ReportRequestItem key={report.reportId} report={report} socket={socket} init={init} />
                     })
                  ) : (
                     <div className="absolute top-0 bottom-0 left-0 right-0 flex items-center justify-center text-3xl text-slate-400 text-bold">
                        No more reports
                     </div>
                  )}
               </div>
            </div>
         </div>
      </>
   )
}

export async function getServerSideProps({ res }) {
   const { locals } = res

   if (!locals.session) {
      return {
         notFound: true,
      }
   }

   const userInfo: UserInfo = locals.session.userInfo
   const userId = userInfo.userId

   let userProfile: UserProfile
   let initReportList: Report[]
   try {
      userProfile = (await axios.get(`${env.javaServerUrl}/profile/id/${userId}`)).data['data']
      initReportList = (await axios.get(`${env.javaServerUrl}/report`)).data['data']
      initReportList = await Promise.all(
         initReportList.map(async (report) => {
            return {
               ...report,
               reporterProfile: (await axios.get(`${env.javaServerUrl}/profile/id/${report.reporter}`)).data['data'],
               reportedProfile: (await axios.get(`${env.javaServerUrl}/profile/id/${report.reported}`)).data['data'],
               reportedUserInfo: (await axios.get(`${env.javaServerUrl}/user/id/${report.reported}`)).data['data'],
            }
         }),
      )
   } catch (err) {
      return {
         notFound: true,
      }
   }

   return {
      props: {
         userProfile,
         initReportList,
         role: userInfo.role,
      },
   }
}

export default Reports
