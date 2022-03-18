import { Image } from '@mantine/core'
import { useEffect, useState } from 'react'

function BlobImage({ file }: { file: globalThis.File | string }) {
   const [payload, setPayload] = useState(null)

   useEffect(() => {
      if (file instanceof globalThis.File) {
         const reader = new FileReader()
         reader.onload = (e) => setPayload(e.target.result as string)
         reader.readAsDataURL(file)
      } else setPayload(file)
   }, [file])

   return <Image radius={10} src={payload} width={250} height={250} alt="" />
}

export default BlobImage
