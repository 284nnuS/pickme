import TinderCard from 'react-tinder-card'
import { useState, createRef, useMemo, useRef } from 'react'
import { SwipeButton } from '.'
import Card from './Card'

const people = [
   {
      name: 'Thu Hoai',
      age: 20,
      url: '../../public/static/images/angry.png',
      bio: 'bla bla',
      voice: '...',
   },
   {
      name: 'Thu Hoai1',
      age: 20,
      url: '../../public/static/images/angry16.png',
      bio: 'bla bla',
      voice: '...',
   },
   {
      name: 'Thu Hoai2',
      age: 20,
      url: '../../public/static/images/angry32.png',
      bio: 'bla bla',
      voice: '...',
   },
   {
      name: 'Thu Hoai3',
      age: 20,
      url: '../../public/static/images/facebook.png',
      bio: 'bla bla',
      voice: '...',
   },
]

const PickmeCard = () => {
   const [currentIndex, setCurrentIndex] = useState(people.length - 1)
   // used for outOfFrame closure
   const currentIndexRef = useRef(currentIndex)

   const childRefs = useMemo(
      () =>
         Array(people.length)
            .fill(0)
            .map((i) => createRef()),
      [],
   )

   const updateCurrentIndex = (val) => {
      setCurrentIndex(val)
      currentIndexRef.current = val
   }

   const canSwipe = currentIndex >= 0

   // set last direction and decrease current index
   const swiped = (direction, nameToDelete, index) => {
      updateCurrentIndex(index - 1)
   }

   const outOfFrame = (name, idx) => {
      console.log(`${name} (${idx}) left the screen!`, currentIndexRef.current)
      // handle the case in which go back is pressed before card goes outOfFrame
      currentIndexRef.current >= idx && childRefs[idx].current.restoreCard()
      // TODO: when quickly swipe and restore multiple times the same card,
      // it happens multiple outOfFrame events are queued and the card disappear
      // during latest swipes. Only the last outOfFrame event should be considered valid
   }

   const swipe = (dir) => {
      if (canSwipe && currentIndex < people.length) {
         childRefs[currentIndex].current.swipe(dir) // Swipe the card!
      }
   }

   return (
      <center>
         <div className="h-[720px] w-[400px] relative">
            {people.map((person, index) => (
               <TinderCard
                  ref={childRefs[index]}
                  className="absolute"
                  key={person.name}
                  onSwipe={(dir) => swiped(dir, person.name, index)}
                  onCardLeftScreen={() => outOfFrame(person.name, index)}
               >
                  <Card data={person} />
               </TinderCard>
            ))}
            <SwipeButton
               handleCloseBtn={() => swipe('left')}
               handleHeartBtn={() => swipe('right')}
               handleRepeatBtn={() => swipe('right')}
               handleStarBtn={() => swipe('right')}
            />
         </div>
      </center>
   )
}

export default PickmeCard
