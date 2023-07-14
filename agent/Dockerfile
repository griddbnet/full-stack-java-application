FROM maven

WORKDIR /app

COPY pom.xml ./

COPY src ./src
COPY tests ./tests
COPY conf ./conf

RUN mvn install
RUN touch log-half-second.txt
CMD ["java", "-jar", "target/GridLogAgent.jar"]
