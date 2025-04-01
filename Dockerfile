FROM amazoncorretto:21
EXPOSE 8080
COPY target/medical-clinic-0.0.1-SNAPSHOT.jar app/medical-clinic-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java",  "-jar", "app/medical-clinic-0.0.1-SNAPSHOT.jar"]