FROM maven:3.8.4-jdk-11 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn package

FROM adoptopenjdk/openjdk11:jre-11.0.9_11.1-alpine AS runner
WORKDIR /app
RUN addgroup -g 1001 -S appgroup
RUN adduser -S appuser -u 1001
COPY --from=builder --chown=appuser:appgroup /app/target/ .

USER appuser
EXPOSE 3001
ENV PORT 3001

CMD ["sh", "-c", "/opt/java/openjdk/bin/java -Djava.net.preferIPv4Stack=true -Dthorntail.http.port=$PORT -jar pickme_restful_api-thorntail.jar"]