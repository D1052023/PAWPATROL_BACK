FROM openjdk:21-jdk-slim
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "target/PAWPATROL_BACK-0.0.5.jar"] 
