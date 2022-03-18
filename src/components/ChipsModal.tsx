import { Modal, Tooltip } from '@mantine/core'
import classNames from 'classnames'
import { Dispatch, SetStateAction } from 'react'

function ChipsModal({
   allChips,
   chips,
   setChips,
   opened,
   setOpened,
}: {
   allChips: InterestChip[]
   chips: InterestChip[]
   setChips: Dispatch<SetStateAction<InterestChip[]>>
   opened: boolean
   setOpened: Dispatch<SetStateAction<boolean>>
}) {
   return (
      <Modal opened={opened} onClose={() => setOpened(false)} centered title="Select your interest">
         <div className="flex flex-wrap gap-1">
            {Object.values(allChips).map((chip) => {
               const idx = chips.indexOf(chip)
               return (
                  <Tooltip key={chip.name} label={`${chip.description}`} allowPointerEvents>
                     <button
                        className={classNames(
                           'h-8 px-3 text-xs leading-8 capitalize border rounded-full bg-slate-100',
                           idx > -1 && 'border-blue-600',
                        )}
                        onClick={() =>
                           setChips((cc) => {
                              if (idx === -1) return [...cc, chip]
                              cc.splice(idx, 1)
                              return [...cc]
                           })
                        }
                     >
                        {chip.name}
                     </button>
                  </Tooltip>
               )
            })}
         </div>
      </Modal>
   )
}

export default ChipsModal
