FROM openjdk:8

RUN mkdir /app
VOLUME /app
COPY . /app
RUN apt update && apt install -y maven
ENTRYPOINT mvn spring-boot:run -f /app/
EXPOSE 9777