import { Modal, Tooltip } from '@mantine/core'
import { useState } from 'react'

function ChipsModal({ allChips, chips, setChips, opened, setOpened }) {
   return (
      <Modal opened={opened} onClose={() => setOpened(false)} centered title="Select your interest">
         {Object.values(allChips)
            .filter((el) => chips.indexOf(el) === -1)
            .map((chip) => {
               return (
                  <Tooltip key={chip} label={`${chip.description}`} allowPointerEvents>
                     <button
                        className="h-8 px-3 text-xs leading-8 capitalize border rounded-full bg-slate-100"
                        onClick={() => setChips((cc) => [...cc, chip])}
                     >
                        {chip.name}
                     </button>
                  </Tooltip>
               )
            })}
      </Modal>
   )
}

export default ChipsModal
