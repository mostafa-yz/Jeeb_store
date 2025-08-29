
FROM gradle:8.7-jdk21 AS build

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


# The output JAR will be in build/libs/
RUN ./gradlew bootJar -x test


FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the 'build' stage
# The name of the JAR file might be different, you can check your `build/libs` folder.


COPY --from=build /app/build/libs/*.jar /app/jeeb_store.jar

# Expose the port your Spring Boot application runs on
EXPOSE 8020

# Command to run the application when the container starts
CMD ["java", "-jar", "jeeb_store.jar"]


