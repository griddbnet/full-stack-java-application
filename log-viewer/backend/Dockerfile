FROM maven

WORKDIR /app

COPY pom.xml ./

COPY src ./src
# COPY mvnw /app/mvnw
# COPY .mvn /app/.mvn

# RUN /app/mvnw verify
RUN mvn dependency:resolve
RUN mvn package
CMD ["java", "-jar", "target/Log-Viewer-1.jar"]