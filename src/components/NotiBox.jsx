import React, { useState } from 'react'
import { IoMdNotifications } from 'react-icons/io'
import NotiForm from './NotiForm';
function NotiBox(notification) {
    notification = [
        {
            id: 1,
            avt: 'https://scontent.fhan2-2.fna.fbcdn.net/v/t1.6435-9/109946554_1232222130460212_2377571917506715282_n.jpg?_nc_cat=106&ccb=1-5&_nc_sid=174925&_nc_ohc=aiRvbcjCvOEAX8zPxsh&_nc_ht=scontent.fhan2-2.fna&oh=00_AT9fGurqNp0-4rR2cZz9H3ooR-TE7bbYurZvqIE-wT_ZrA&oe=62469F94',
            content: 'You have a new matched!',
            time: '5 hours ago'
        },
        {
            id: 2,
            avt: 'https://scontent.fhan2-1.fna.fbcdn.net/v/t1.6435-9/161332017_1424586734557083_8146959451217062898_n.jpg?_nc_cat=101&ccb=1-5&_nc_sid=174925&_nc_ohc=ytiOYgz9dd8AX9w3Rma&_nc_ht=scontent.fhan2-1.fna&oh=00_AT-cGK49Qa_IXlEVKZHwA_V_SjvGcf4VFKnxPb0vJ1-vyA&oe=6244CF65',
            content: 'You have a new matched!',
            time: '5 hours ago'
        },
        {
            id:3,
            avt: 'https://scontent.fhan2-4.fna.fbcdn.net/v/t1.6435-9/120553970_1297830167232741_7820364494808289953_n.jpg?_nc_cat=103&ccb=1-5&_nc_sid=174925&_nc_ohc=JoYv_LEMDwIAX99jTRd&_nc_ht=scontent.fhan2-4.fna&oh=00_AT8VMy6ayAawo494s1yQEGN9WizsE-rmVjz68_DnQzmQxg&oe=624662C0',
            content: 'Your profile has been reported for violating our community standards!',
            time: '10 hours ago'
        }
    ]
    const [onShow, setShow] = useState(true);
    const [onHide, setHide] = useState(false);
    const [count, setCount] = useState(notification.length);
    const handleHide = () => {
        setShow(false)
        setHide(true)
        setCount(0)
    }
    const handleShow = () => {
        setShow(true)
        setHide(false)
    }

    return (


        <div className="w-[26rem] h-screen ">
            {onShow && (
                <div className='static pb-2 pt-2 pl-2' onClick={handleHide} >
                    <button>
                        <div className='w-10 h-10 rounded-full grid grid-cols-1 bg-[#69c0c7] place-items-center'>
                            <div>
                                <IoMdNotifications className='text-white w-6 h-6 ' />
                            </div>
                        </div>
                    </button>
                    <button className='ml-3 absolute top-9 left-4 '>
                        {count > 0 &&
                            <div className='w-6 h-6 rounded-full grid grid-cols-1 bg-[#fe4457]'>
                                <div className='text-white text-center'>{count}</div>
                            </div>}
                    </button>
                </div>
            )

            }

            {onHide && (
                <div>
                    <div className='static pb-2 pt-2 pl-2' onClick={handleShow}>
                        <button>
                            <div className='w-10 h-10 rounded-full grid grid-cols-1 bg-[#69c0c7] place-items-center '>
                                <div>
                                    <IoMdNotifications className='text-white w-6 h-6 ' />
                                </div>
                            </div>
                        </button>

                    </div>
                    <div className='w-[28rem]  ml-2 border-solid border-2 border-[#69c0c7] rounded-lg bg-[#e0edfc]' >
                    {notification.map((noti) => <NotiForm key={noti.id} {...noti} />)}

                    </div>
                </div>
            )}




        </div>
    );

}

export default NotiBox