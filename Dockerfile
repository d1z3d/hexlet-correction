FROM docker:dind-rootless
USER root
RUN apk add --no-cache openjdk21
RUN apk add --no-cache gradle
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle clean build --no-daemon
