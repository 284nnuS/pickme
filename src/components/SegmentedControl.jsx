import { useRef } from 'react'

function SegmentedControl({ data, value, setValue }) {
   const radioRefs = useRef({})
   return (
      <div>
         <label className="block text-sm font-bold text-gray-900 dark:text-gray-400">Gender</label>
         <fieldset className="grid grid-cols-3 mt-2 gap-x-1" onChange={(e) => setValue(e.target.value)} value={value}>
            {data.map((el, i) => {
               return (
                  <div key={el.name} className="w-32 h-12">
                     <input
                        type="radio"
                        name="gender"
                        value={el.name}
                        ref={(ref) => (radioRefs.current[i] = ref)}
                        className="hidden peer"
                        required
                     />
                     <button
                        type="button"
                        className="w-full h-full text-sm text-center text-gray-900 bg-white border border-gray-300 rounded-lg peer-checked:bg-gray-900 peer-checked:text-white"
                        onClick={() => radioRefs.current[i].click()}
                     >
                        {el.label}
                     </button>
                  </div>
               )
            })}
         </fieldset>
      </div>
   )
}

export default SegmentedControl
