
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

COPY src ./src

EXPOSE 8080

CMD ["java", "-jar", "target/techservices-api-1.0.0.jar"]
