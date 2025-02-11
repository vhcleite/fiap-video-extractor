FROM amazoncorretto:21

RUN yum update -y && \
    yum install -y ffmpeg ffmpeg-devel

ENV ENVIRONMENT=dev

ADD target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]