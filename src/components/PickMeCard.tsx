import TinderCard from 'react-tinder-card'
import { useState, createRef, useMemo, useRef, useEffect, useCallback, RefObject } from 'react'
import { SwipeButton } from '.'
import Card from './Card'
import axios from 'axios'
import { useKeyPressEvent } from 'react-use'
import { Socket } from 'socket.io-client'
import { useThrottleCallback } from '@react-hook/throttle'

function PickMeCard({
   defaultInterests,
   socket,
   init,
}: {
   defaultInterests: InterestChip[]
   socket: Socket
   init: boolean
}) {
   const [peopleList, setPeopleList] = useState<Card[]>([])
   const [needFetch, setNeedFetch] = useState(true)
   const [currentCard, setCurrentCard] = useState(0)

   const process = (peoples: Card[]) =>
      peoples.filter((v: Card, i: number, a: Card[]) => a.findIndex((t) => t.userId === v.userId) === i)

   const calcAge = (birthday: number) => {
      const current = new Date().getTime()
      return Math.trunc((current - birthday) / (1000 * 60 * 60 * 24 * 365))
   }

   useEffect(() => {
      socket.on('Reroll', (cards: Card[]) => {
         setPeopleList(
            cards.map((el) => {
               const result = {
                  ...el,
                  voice: el.medias.find((el: Media) => el.mediaType === 'voice'),
                  images: el.medias.filter((el: Media) => el.mediaType === 'image'),
               }
               delete result.medias
               return result
            }),
         )
      })
      socket.on('Cards', (cards: Card[]) => {
         setPeopleList((current) =>
            process([
               ...current,
               ...cards.map((el) => {
                  const result = {
                     ...el,
                     voice: el.medias.find((el: Media) => el.mediaType === 'voice'),
                     images: el.medias.filter((el: Media) => el.mediaType === 'image'),
                  }
                  delete result.medias
                  return result
               }),
            ]),
         )
      })
   }, [])

   useEffect(() => {
      if (!init || !needFetch) return
      socket.emit('Get cards')
   }, [init, needFetch])

   const swipe = (dir: string, id: number) => {
      setCurrentCard((current) => current + 1)
      socket.emit('Swipe', { id, like: dir === 'right' })
   }

   const superLike = useThrottleCallback(
      () => {
         socket.emit('React', { id: peopleList[currentCard].userId, name: peopleList[currentCard].name })
         cardRef[peopleList.length - 1 - currentCard].current?.swipe('right')
      },
      1,
      true,
   )

   useKeyPressEvent('ArrowUp', superLike)
   useKeyPressEvent('ArrowLeft', () => cardRef[peopleList.length - 1 - currentCard].current?.swipe('left'))
   useKeyPressEvent('ArrowRight', () => cardRef[peopleList.length - 1 - currentCard].current?.swipe('right'))

   const cardRef = peopleList.reduce((acc, val, i) => {
      acc[i] = createRef()
      return acc
   }, {} as Record<number, RefObject<any>>)

   return (
      <div className=" h-[75%] aspect-[5/9] m-2 rounded-2xl bg-slate-200 relative">
         {peopleList
            .slice()
            .reverse()
            .map((person, i, l) => (
               <TinderCard
                  ref={cardRef[i]}
                  className="absolute z-10 w-full h-full select-none"
                  key={person.name}
                  preventSwipe={['up', 'down']}
                  onSwipe={(dir) => swipe(dir, person.userId)}
                  onCardLeftScreen={() => {
                     setPeopleList((current) => [...current.slice(1, current.length)])
                     setCurrentCard((current) => current - 1)
                  }}
                  // onCardLeftScreen={() => outOfFrame(person.name, index)}
               >
                  <Card
                     {...person}
                     age={calcAge(person.birthday)}
                     defaultInterests={defaultInterests}
                     isFirst={currentCard === l.length - 1 - i}
                  />
               </TinderCard>
            ))}
         <SwipeButton
            handleCloseBtn={() => {
               /**/
            }}
            handleHeartBtn={superLike}
            handleRepeatBtn={() => socket.emit('Reroll', peopleList[currentCard].userId)}
            handleStarBtn={() => {
               /**/
            }}
         />
         <div className="absolute top-0 bottom-0 left-0 right-0 flex items-center justify-center text-2xl font-semibold text-slate-500">
            No card left
         </div>
      </div>
   )
}

export default PickMeCard
