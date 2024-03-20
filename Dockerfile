FROM openjdk:17-alpine as build
COPY . /usr/app
WORKDIR /usr/app
RUN chmod +x mvnw \
    && ./mvnw --version \
    && ./mvnw clean package

FROM openjdk:17-alpine
COPY --from=build /usr/app/target/*.jar app.jar

VOLUME /resource
COPY /resource /resource

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

