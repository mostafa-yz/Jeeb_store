# --- STAGE 1: Build the application ---
# Use a Gradle image with JDK 17 to build the project.
# Change to 'maven:3.8.7-openjdk-17' if you are using Maven.
FROM gradle:8.7-jdk17 AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle build files and source code
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

# Make the Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the application, skipping tests to speed up the process
# The output JAR will be in build/libs/
RUN ./gradlew bootJar -x test

# --- STAGE 2: Create the final, lightweight runtime image ---
# Use a lightweight JRE (Java Runtime Environment) image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the 'build' stage
# The name of the JAR file might be different, you can check your `build/libs` folder.
# It's often named like `your-project-name-0.0.1-SNAPSHOT.jar`

COPY --from=build /app/build/libs/*.jar /app/jeeb_store.jar

# Expose the port your Spring Boot application runs on
EXPOSE 8020

# Command to run the application when the container starts
CMD ["java", "-jar", "jeeb_store.jar"]


