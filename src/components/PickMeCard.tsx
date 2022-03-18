import TinderCard from 'react-tinder-card'
import { useState, createRef, useEffect, RefObject } from 'react'
import { SwipeButton } from '.'
import Card from './Card'
import { useKeyPressEvent, useSessionStorage } from 'react-use'
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
   const [needFetch] = useState(true)
   const [currentCard, setCurrentCard] = useState(0)

   const process = (peoples: Card[]) =>
      peoples.filter((v: Card, i: number, a: Card[]) => a.findIndex((t) => t.userId === v.userId) === i)

   const calcAge = (birthday: number) => {
      const current = new Date().getTime()
      return Math.trunc((current - birthday) / (1000 * 60 * 60 * 24 * 365))
   }

   useEffect(() => {
      socket.on('Reroll', (cards: Card[]) => {
         setPeopleList(cards)
      })
      socket.on('Cards', (cards: Card[]) => {
         setPeopleList((current) => process([...current, ...cards]))
      })
   }, [])

   const [value, setValue] = useSessionStorage('persist-cards')

   useEffect(() => {
      if (!init || !needFetch) return
      if (value) {
         setPeopleList(value as Card[])
         setValue(null)
      } else socket.emit('Get cards')
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
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
   }, {} as Record<number, RefObject<any>>)

   const reroll = useThrottleCallback(() => socket.emit('Reroll'), 1, true)

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
               >
                  <Card
                     {...person}
                     age={calcAge(person.birthday)}
                     defaultInterests={defaultInterests}
                     isFirst={currentCard === l.length - 1 - i}
                     onNavigate={() => setValue(peopleList)}
                  />
               </TinderCard>
            ))}
         <SwipeButton
            handleCloseBtn={() => {
               /**/
            }}
            handleHeartBtn={superLike}
            handleRepeatBtn={reroll}
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
