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
│ ├── app -> Web application pages
│ ├── auth -> Auth pages
├── components -> React components
└── styles -> CSS
```

## Configuration

All .env files put in configs folder

-  nextjs.env

   -  GOOGLE_CLIENT_ID
   -  GOOGLE_CLIENT_SECRET
   -  SECRET_KEY
   -  NEXT_PUBLIC_URL (\*)
   -  NEXTAUTH_URL (\*)

-  java.env
   -  DB_HOSTNAME (default: "localhost")
   -  DB_NAME (default: "DATABASE")
   -  DB_USERNAME (default: "localhost")
   -  DB_PASSWORD
-  db.env
   -  MYSQL_ROOT_PASSWORD

### Note:

-  (\*) Both have the same value

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
