import { IoMdNotifications } from 'react-icons/io'
import React from 'react'

const NotiForm = ({avt,content, time}) => {
    return (
        <div >

            <div className=''>
                <button type='button' className=' mt-2 mb-2 pt-1 pb-6  flex justify-start hover:bg-[#d2e0ed] hover:rounded-xl  focus:bg-slate-300 focus:rounded-2xl'>
                    <div className='w-[4rem] h-[3rem] bg-cover bg-center ml-5'>

                        <div className='static'>
                            <img className='rounded-full' src={avt} />
                        </div>
                        <div className='w-5 h-5 rounded-full grid grid-cols-1 bg-[#ee7a1f] place-items-center relative bottom-4  '>
                            <IoMdNotifications className='text-black w-4 h-4 ' />
                        </div>

                    </div>
                    <div className='ml-5 w-[20rem] mr-5 '>
                        <div className='text-left '>{content}</div>
                        <div className='text-gray-400 text-left'>{time}</div>
                    </div>

                </button>
            </div>
        </div>
    )
}
export default NotiForm
