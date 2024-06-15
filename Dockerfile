FROM docker:dind
VOLUME /var/run/docker.sock:/var/run/docker.sock
RUN chmod a+rw /var/run/docker.sock
RUN apk add --no-cache openjdk21
RUN apk add --no-cache gradle
WORKDIR /app
COPY . /app
RUN ./gradlew clean build --stacktrace
