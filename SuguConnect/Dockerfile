# Étape 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Runtime
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copier le JAR
COPY --from=build /app/target/*.jar app.jar

# Exposer le port configuré par Render
EXPOSE 7878

# Démarrage de l’application
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
