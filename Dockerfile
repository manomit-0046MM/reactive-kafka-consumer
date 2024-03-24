FROM eclipse-temurin:17-jdk-focal
ARG db_url
ENV DB_URL=$db_url
ARG db_user
ENV DB_USER=$db_user
ARG db_password
ENV DB_PASSWORD=$db_password
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
ARG sr_api_secret
ENV SR_API_SECRET=$sr_api_secret
WORKDIR /app
ADD . .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
EXPOSE 8081
CMD ["./mvnw", "spring-boot:run"]