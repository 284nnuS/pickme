import http from 'http'
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const has = function (obj: any, key: string): boolean {
   const keyParts = key.split('.')
   return (
      !!obj &&
      (keyParts.length > 1
         ? has(obj[key.split('.')[0]], keyParts.slice(1).join('.'))
         : Object.prototype.hasOwnProperty.call(obj, key))
   )
}

export function parseCookies(request: http.IncomingMessage): Record<string, string> {
   const list: Record<string, string> = {}
   const cookieHeader = request.headers?.cookie
   if (!cookieHeader) return list

   cookieHeader.split(`;`).forEach(function (cookie: string) {
      // eslint-disable-next-line prefer-const
      let [name, ...rest] = cookie.split(`=`)
      name = name?.trim()
      if (!name) return
      const value = rest.join(`=`).trim()
      if (!value) return
      list[name] = decodeURIComponent(value)
   })

   return list
}
