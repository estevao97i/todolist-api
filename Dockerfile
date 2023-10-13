FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

COPY . .

RUN apt-get install maven -y
#RUN apt-get clean install
RUN mvn clean package -Pprod -DskipTests

FROM openjdk:17-jdk-slim

COPY --from=build /target/todolist-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]