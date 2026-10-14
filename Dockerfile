# ---------- Stage 1: build the WAR with Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---------- Stage 2: run on Tomcat 9 (javax.servlet, same as the syllabus) ----------
FROM tomcat:9.0-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Database file location (mount a persistent disk here in production)
ENV DB_PATH=/usr/local/tomcat/data/lifelink
RUN mkdir -p /usr/local/tomcat/data

EXPOSE 8080
CMD ["catalina.sh", "run"]
