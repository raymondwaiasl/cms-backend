FROM openjdk:17-alpine as build
COPY . /usr/app
WORKDIR /usr/app
RUN chmod +x mvnw \
    && ./mvnw --version \
    && ./mvnw clean package -DskipTests

FROM openjdk:17-alpine
COPY --from=build /usr/app/target/*.jar app.jar

VOLUME /resource
COPY /resource /resource

COPY read_secrets.sh /opt/read_secrets.sh
RUN chmod +x /opt/read_secrets.sh

EXPOSE 8080

ENTRYPOINT ["/opt/read_secrets.sh"]
CMD ["java","-jar","app.jar"]

