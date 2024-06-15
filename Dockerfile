FROM docker:dind
RUN apk add --no-cache openjdk21
RUN apk add --no-cache gradle
WORKDIR /app
COPY . /app
RUN chmod 777 ./gradlew
RUN ./gradlew clean build --stacktrace
