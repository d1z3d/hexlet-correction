FROM docker:dind-rootless
RUN apk add openjdk21
RUN apk add gradle
WORKDIR /app
COPY . /app
RUN gradle clean build --no-daemon --stacktrace
