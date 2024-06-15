FROM docker:dind
ARG DOCKER_HOST=unix://$XDG_RUNTIME_DIR/docker.sock
RUN apk add --no-cache openjdk21
RUN apk add --no-cache gradle
WORKDIR /app
COPY . /app
RUN gradle clean build --no-daemon
