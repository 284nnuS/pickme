import { Tooltip } from '@mantine/core'
import { MdOutlineClose } from 'react-icons/md'

function Chips({ values, setValues }) {
   return (
      <div className="flex flex-wrap gap-x-2 gap-y-2">
         {Object.values(values).map((v, i) => {
            return (
               <Tooltip key={v} label={`${v.description}`}>
                  <div className="relative h-8 px-3 text-xs leading-8 capitalize border rounded-full bg-slate-100">
                     {v.name}
                     <button
                        className="absolute w-4 h-4 p-0.5 bg-red-600 rounded-full hover:bg-red-800 -right-1.5 -bottom-1.5"
                        onClick={() =>
                           setValues((cv) => {
                              cv.splice(i, 1)
                              return [...cv]
                           })
                        }
                     >
                        <MdOutlineClose className="w-full h-full text-white" />
                     </button>
                  </div>
               </Tooltip>
            )
         })}
      </div>
   )
}

export default Chips
