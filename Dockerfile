FROM node:16-alpine as dependencies
RUN apk add --no-cache libc6-compat
WORKDIR /app
COPY package.json yarn.lock ./
RUN yarn install --frozen-lockfile

FROM node:16-alpine as builder
WORKDIR /app
ENV NODE_ENV production
COPY . .
COPY --from=dependencies /app/node_modules ./node_modules
RUN yarn build:next && yarn build:server && yarn install --production --ignore-scripts --prefer-offline

FROM node:16-alpine AS runner
WORKDIR /app
RUN npm install pm2 -g
ENV NODE_ENV production
RUN addgroup -g 1001 -S appgroup
RUN adduser -S appuser -u 1001

COPY --from=builder /app/public ./public
COPY --from=builder --chown=appuser:appgroup /app/.next ./.next
COPY --from=builder --chown=appuser:appgroup /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

USER appuser
EXPOSE 3000
ENV PORT 3000

CMD ["yarn", "start:server"]