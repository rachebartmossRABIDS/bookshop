FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY javaEEProject4/pom.xml ./pom.xml
RUN mvn -q -DskipTests dependency:go-offline
COPY javaEEProject4/src ./src
RUN cd src/main/resources && for f in *.properties.example; do cp "$f" "${f%.example}"; done
RUN mvn -q -DskipTests package

FROM tomcat:9-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /workspace/target/javaEEProject4-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/project1.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
