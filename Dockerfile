    FROM openjdk:17
    LABEL maintainer="Alexey <aliakseiliyutich@gmail.com>"
    ADD build/libs/blog-0.0.1-SNAPSHOT.jar /app/
    ENTRYPOINT ["java", "-jar", "/app/blog-0.0.1-SNAPSHOT.jar"]
