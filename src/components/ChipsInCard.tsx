import { Tooltip } from '@mantine/core'

function ChipsInCard({ values }: { values: InterestChip[] }) {
   return (
      <div className="flex flex-wrap gap-x-2 gap-y-2">
         {Object.values(values).map((v, i) => {
            return (
               <Tooltip key={v.name} label={`${v.description}`}>
                  <div className="relative h-8 px-3 font-semibold leading-8 text-white capitalize bg-teal-800 rounded-full text-md">
                     {v.name}
                  </div>
               </Tooltip>
            )
         })}
      </div>
   )
}

export default ChipsInCard
