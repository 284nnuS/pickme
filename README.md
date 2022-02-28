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
├── .husky
├── configs -> Environment configs, etc
├── java
│   └── src
│       ├── main/java/tech/zoomidsoon/pickme_restful_api
│       │	├── configs	-> Java configs
│       │	├── controllers
│       │	├── helpers
│       │	├── mappers	-> SQL Row mappers
│       │	├── models
│       │	├── repos
│       │	└── utils
│       └── test/java
│           └── helpers
├── scripts		        -> Helpful scripts
├── server		        -> ExpressJS server code
│   └── routes
├── public      		-> Website assets
├── shared      		-> Shared between server-side and client-side
├── sql
└── src
    ├── components             -> React components
    ├── pages
    │   ├── app
    │   └── auth 		-> Auth pages
    └── styles
```

## Configuration

All .env files put in configs folder

-  nextjs.env

   -  GOOGLE_CLIENT_ID
   -  GOOGLE_CLIENT_SECRET
   -  SECRET_KEY
   -  JAVA_SERVER_URL
   -  NEXTAUTH_URL

-  java.env
   -  DB_HOSTNAME (default: "localhost")
   -  DB_NAME (default: "DATABASE")
   -  DB_USERNAME (default: "root")
   -  DB_PASSWORD
-  db.env
   -  MYSQL_ROOT_PASSWORD
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
