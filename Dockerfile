FROM docker:dind 
RUN apk add --no-cache openjdk21
RUN apk add --no-cache gradle
WORKDIR /app
COPY . /app
RUN chmod -R 755 /app
RUN gradle clean build --no-daemon
