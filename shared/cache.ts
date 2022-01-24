/* eslint-disable @typescript-eslint/no-explicit-any */
import NodeCache from 'node-cache'
const fnCache = new NodeCache()

export function cache<T>(fn: (...args: any[]) => T, ...args: any[]): T {
   const key = fn.name + '|' + args.map((el) => el.toString()).join('__')
   let result: T = fnCache.get(key)
   if (result) return result
   result = fn.call(null, ...args)
   fnCache.set(key, result)
   return result
}

export async function cacheAsync<T>(fn: (...args: any[]) => Promise<T>, ...args: any[]): Promise<T> {
   const key = fn.name + '|' + args.map((el) => el.toString()).join('__')
   const cached: T = fnCache.get(key)
   if (cached) return new Promise<T>((resolve) => resolve(cached))
   return new Promise<T>((resolve, reject) => {
      const promise: Promise<T> = fn.call(null, ...args)
      promise
         .then((result: T) => {
            fnCache.set(key, result)
            resolve(result)
         })
         .catch((err: any) => reject(err))
   })
}
