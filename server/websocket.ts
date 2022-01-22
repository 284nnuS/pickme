import { Server, Socket } from 'socket.io'
import http from 'http'

const sio = new Server()

sio.on('connection', (socket: Socket) => {
   socket.emit('message', 'Hello from Socket.IO')

   socket.on('disconnect', () => {
      console.log('client disconnected')
   })
})

export default function attach(server: http.Server) {
   sio.attach(server)
}
