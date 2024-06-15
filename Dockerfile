FROM docker:dind
USER root
RUN apk add --no-cache openjdk21
RUN apk add --no-cache gradle
WORKDIR /app
COPY . /app
RUN gradle clean build --no-daemon
