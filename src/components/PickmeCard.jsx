import Player from './Player'

function PickmeCard(data) {
   return (
      <div className="flex justify-center w-screen h-screen">
         <div
            style={{
               backgroundImage: `url(https://scontent.fdad3-5.fna.fbcdn.net/v/t1.6435-9/146994747_2854728808188423_7615033960248435066_n.jpg?_nc_cat=106&ccb=1-5&_nc_sid=09cbfe&_nc_ohc=gtQZRbkkXwEAX-XNyGy&_nc_ht=scontent.fdad3-5.fna&oh=00_AT8N16aeFbtlFQ2wMA86wlHCqxM779f34tV0YBFpSUVwQQ&oe=62432D08)`,
            }}
            className="relative w-[410px] h-[720px] rounded-2xl bg-cover bg-center flex flex-col justify-between"
         >
            <Player url="/a.mp3" />

            <div>
               <h3 className=" pl-2 font-sans text-white font-black text-4xl ">Elon Sơn</h3>
               <h5 className=" pl-2 font-sans text-white text-xl mb-[15px] ">
                  {' '}
                  Cuộc đời chưa có như cuộc sống, bởi vì cuộc sống không phải cuộc đời{' '}
               </h5>
            </div>
         </div>
      </div>
   )
}
export default PickmeCard
