#
# Build stage
#
FROM eclipse-temurin:17-jdk-jammy AS build
ENV HOME=/usr/app
ARG db_url
ENV DB_URL=$db_url
ARG db_user
ENV DB_USER=$db_user
ARG db_password
ENV DB_PASSWORD=$db_password
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 ./mvnw -f $HOME/pom.xml clean install

#
# Package stage
#
FROM eclipse-temurin:17-jre-jammy
ARG bootstrap_server
ENV BOOTSTRAP_SERVER=$bootstrap_server
ARG sr_url
ENV SR_URL=$sr_url
ARG sr_api_key
ENV SR_API_KEY=$sr_api_key
ARG cluster_api_key
ENV CLUSTER_API_KEY=$cluster_api_key
ARG cluster_api_secret
ENV CLUSTER_API_SECRET=$cluster_api_secret
ENV DB_URL=$db_url
ENV DB_USER=$db_user
ENV DB_PASSWORD=$db_password
ARG sr_api_secret
ENV SR_API_SECRET=$sr_api_secret
ARG JAR_FILE=/usr/app/target/REACTIVE_KAFKA_CONSUMER.jar
COPY --from=build $JAR_FILE /app/runner.jar
EXPOSE 8081
ENTRYPOINT java -jar /app/runner.jar