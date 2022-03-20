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
      socket.on('reroll', (cards: Card[]) => {
         setPeopleList(cards)
      })
      socket.on('cards', (cards: Card[]) => {
         setPeopleList((current) => process([...current, ...cards]))
      })
   }, [])

   const [value, setValue] = useSessionStorage('persist-cards')

   useEffect(() => {
      if (!init || !needFetch) return
      if (value) {
         setPeopleList(value as Card[])
         setValue(null)
      } else socket.emit('card:get')
   }, [init, needFetch])

   const swipe = (dir: string, id: number) => {
      setCurrentCard((current) => current + 1)
      socket.emit('card:swipe', { id, like: dir !== 'left' })
   }

   const superLike = useThrottleCallback(
      () => {
         if (currentCard >= peopleList.length) return
         socket.emit('profile:react', { id: peopleList[currentCard].userId, name: peopleList[currentCard].name })
         cardRef[peopleList.length - 1 - currentCard].current?.swipe('up')
      },
      1,
      true,
   )

   useKeyPressEvent('ArrowUp', superLike)
   useKeyPressEvent(
      'ArrowLeft',
      () => currentCard < peopleList.length && cardRef[peopleList.length - 1 - currentCard].current?.swipe('left'),
   )
   useKeyPressEvent(
      'ArrowRight',
      () => currentCard < peopleList.length && cardRef[peopleList.length - 1 - currentCard].current?.swipe('right'),
   )

   const cardRef = peopleList.reduce((acc, val, i) => {
      acc[i] = createRef()
      return acc
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
   }, {} as Record<number, RefObject<any>>)

   const reroll = useThrottleCallback(() => socket.emit('reroll'), 1, true)

   return (
      <div className="h-[85%] md:h-[75%] aspect-[5/9] max-w-screen m-2 rounded-2xl bg-slate-200 relative">
         {peopleList
            .slice()
            .reverse()
            .map((person, i, l) => (
               <TinderCard
                  ref={cardRef[i]}
                  className="absolute z-10 w-full h-full select-none"
                  key={person.name}
                  preventSwipe={['down']}
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
            handleCloseBtn={() => cardRef[peopleList.length - 1 - currentCard].current?.swipe('left')}
            handleHeartBtn={superLike}
            handleRepeatBtn={reroll}
            handleStarBtn={() => cardRef[peopleList.length - 1 - currentCard].current?.swipe('right')}
         />
         <div className="absolute top-0 bottom-0 left-0 right-0 flex items-center justify-center text-2xl font-semibold text-slate-500">
            No card left
         </div>
      </div>
   )
}

export default PickMeCard
