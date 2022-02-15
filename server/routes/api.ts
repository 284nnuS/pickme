import { Express } from 'express'
import { createProxyMiddleware } from 'http-proxy-middleware'

export default function routeAPI(app: Express) {
   const middleware = createProxyMiddleware({
      target: process.env.JAVA_SERVER_URL,
      changeOrigin: true,
      pathRewrite: { '^/api/restful': '' },
   })

   app.all('/api/restful/*', middleware)
}
