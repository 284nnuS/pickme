import React from 'react'
import { Global } from '@emotion/react'
import tw, { css, GlobalStyles as BaseStyles } from 'twin.macro'

const customStyles = css({
   body: {
      WebkitTapHighlightColor: 'rgba(0,0,0,0)',
      ...tw`antialiased`,
   },
   '#__next': {
      height: '100%',
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
   },
})

const GlobalStyles = () => (
   <>
      <BaseStyles />
      <Global styles={customStyles} />
   </>
)

export default GlobalStyles
