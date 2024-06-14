FROM docker:stable-dind 
RUN apk add --no-cache \
    python3 \
    py3-pip \
    openjdk21-jre \
    gradle
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle clean build --no-daemon
