FROM openjdk:23-jdk-oracle AS builder

WORKDIR /src

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true 

ENV PORT=8080

EXPOSE ${PORT}

# i don't need this anymore
# as app will run in second stage

# second stage
FROM openjdk:23-jdk-oracle

WORKDIR /app

COPY --from=builder /src/target/vttp_batchb_miniproj-0.0.1-SNAPSHOT.jar vttp_batchb_miniproj.jar

ENV PORT=8080
ENV movie_db_host=localhost movie_db_port=6379
ENV movie_db_username="" movie_db_password=""

EXPOSE ${PORT}

SHELL [ "/bin/sh", "-c" ]

ENTRYPOINT java -jar vttp_batchb_miniproj.jar

HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 CMD curl -s -f http:/localhost:8080/demo/health || exit 1