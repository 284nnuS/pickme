import { Tooltip } from '@mantine/core'

function ChipsInProfile({ values }: { values: InterestChip[] }) {
   return (
      <div className="flex flex-wrap gap-x-2 gap-y-2">
         {Object.values(values).map((v) => {
            return (
               <Tooltip key={v.name} label={`${v.description}`}>
                  <div className="relative px-3 font-semibold leading-7 text-white capitalize rounded-full h-7 bg-gradient-to-br from-blue-600 to-cyan-600 text-md">
                     {v.name}
                  </div>
               </Tooltip>
            )
         })}
      </div>
   )
}

export default ChipsInProfile
