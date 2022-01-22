# PickMe

> Online social networking application that provides chatting, making friend service by matching the right person with users based on interests, voice records, and pictures

## Requirement

-  NodeJS == 16
-  JDK == 11
-  Yarn
-  Maven

## Install

```sh
yarn
```

## Project structure

```
.
├── java/
│ └── src/main/java/tech/zoomidsoon/pickme_restful_api/
│ ├── configs -> Java configs
│ ├── controllers
│ ├── mappers -> SQL Row mappers
│ ├── models
│ ├── repos -> Repository
│ └── utils
├── configs -> Environment configs, etc
├── public -> Website assets
├── scripts -> Helpful scripts
├── server -> ExpressJS server code
├── shared -> Shared between server-side and client-side
└── src/
├── pages/
│ ├── admin -> Admin pages
│ ├── app -> Web application pages
│ ├── auth -> Auth pages
│ └── moderator -> Moderator pages
├── components -> React components
└── styles -> CSS
```

## Configuration

All .env files put in configs folder

-  nextjs.env
-  java.env
-  db.env

## Start development

```sh
yarn dev
```

### ExpressJS + NextJS

```sh
yarn dev:server
```

### Java RESTFul API Server

```sh
yarn dev:java
```

## Deploy (Docker)

```sh
docker-compose up
```
