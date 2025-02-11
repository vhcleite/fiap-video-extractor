FROM eclipse-temurin:21-jdk

# Install FFmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean

# Set environment variable
ENV ENVIRONMENT=dev

# Add the Spring Boot application JAR
ADD target/app.jar app.jar

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
