export const getDiffTimeToString = (time: number): string => {
   const current = new Date().getTime()
   const diff = current - time
   if (diff > 1000 * 60 * 60 * 24 * 7) return new Date(time).toLocaleDateString()
   if (diff > 1000 * 60 * 60 * 24) {
      const days = Math.trunc(diff / (1000 * 60 * 60 * 24))
      return days + ` day${days > 1 ? 's' : ''} ago`
   }
   if (diff > 1000 * 60 * 60) {
      const hours = Math.trunc(diff / (1000 * 60 * 60))
      return hours + ` hour${hours > 1 ? 's' : ''} ago`
   }
   if (diff > 1000 * 60) {
      const mins = Math.trunc(diff / (1000 * 60))
      return mins + ` min${mins > 1 ? 's' : ''} ago`
   }
   return 'Just now'
}
