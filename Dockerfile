########################################################################################################################
# Stage 0: Download ----------------------------------------------------------------------------------------------------

# FROM alpine/git
# ARG url
# WORKDIR /app
# RUN git clone ${url}
# RUN git clone https://github.com/heroku/java-getting-started.git

########################################################################################################################
# Stage 1: Build -------------------------------------------------------------------------------------------------------

FROM maven:3.9-eclipse-temurin-21 AS build
RUN echo 'First stage...';

# ARG project
# WORKDIR /app
# COPY --from=0 /app/${project} /app
# RUN mvn install

# CMD ["sh", "-c", "echo ${HOME}"]

# COPY src /usr/src/app/src
# COPY pom.xml /usr/src/app
# RUN mvn -f /usr/src/app/pom.xml clean package

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -Ddockerfile.skip=true

########################################################################################################################
# Stage 2: Create minimal Java runtime with JLink ----------------------------------------------------------------------

#FROM eclipse-temurin:17-jdk-alpine AS jlink
#RUN $JAVA_HOME/bin/jlink \
#    --module-path $JAVA_HOME/jmods \
#    --add-modules java.base,java.logging,java.xml,java.naming,java.sql,java.management,java.instrument,jdk.unsupported,java.desktop,java.security.jgss \
#    --output /javaruntime \
#    --compress=2 --no-header-files --no-man-pages

########################################################################################################################
# Stage 3: Final Stage -------------------------------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS runtime
RUN echo 'Final stage...';

ARG DESCRIPTION="HiSudoku backend software, provides access to puzzles collection and user data."
ARG BUILD_TAG=local
ARG VERSION=local
LABEL maintainer="krzysztofbasior"
LABEL description=${DESCRIPTION}
LABEL version=${VERSION}
LABEL build_tag=${BUILD_TAG}

#WORKDIR /app
#COPY --from=jlink /javaruntime /opt/java-minimal
#ENV PATH="/opt/java-minimal/bin:$PATH"
#COPY --from=build /app/target/*.jar /app/app.jar
#EXPOSE 8081
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]

WORKDIR /app

# COPY HealthCheck.java .
# HEALTHCHECK --interval=5s --timeout=3s --retries=4 CMD ["sh", "-c", "java -Durl=https://${HEALTHCHECK_HOST}/actuator/health ./HealthCheck.java || exit 1"]

#COPY --from=build /app/target/hisudoku-api-*.jar /app/app.jar
COPY --from=build /app/target/*.jar /app/app.jar

#ARG artifactid
#ARG version
#ENV artifact ${artifactid}-${version}.jar
#WORKDIR /app
#COPY --from=1 /app/target/${artifact} /app
#EXPOSE 8080
#ENTRYPOINT ["sh", "-c"]
#CMD ["java -jar ${artifact}"]

ENV ACTIVE_PROFILE=prod
RUN if ["$ACTIVE_PROFILE" = "prod"]; \
    then export APPLICATION_PORT="$DEFAULT_APPLICATION_PORT"; \
    elif ["$ACTIVE_PROFILE" = "test"]; \
    then export APPLICATION_PORT="$TEST_APPLICATION_PORT"; \
    else export APPLICATION_PORT="$DEFAULT_APPLICATION_PORT"; \
    fi

ENTRYPOINT ["sh", "-c", "java -server ${JAVA_OPTS} ${CATALINA_OPTS} -Dspring.profiles.active=${ACTIVE_PROFILE} -jar /app/app.jar ${0} ${@}"]
########################################################################################################################
# Note to self ---------------------------------------------------------------------------------------------------------

#docker build --build-arg url=https://github.com/heroku/java-getting-started.git\
#    --build-arg project=java-getting-started\
#    --build-arg artifactid=java-getting-started\
#    --build-arg version=1.0\
#    -t username/java-getting-started - < Dockerfile

# The mvn dependency:go-offline command is used to prepare a Maven project for offline usage. This command downloads all the dependencies, plugins, and other resources specified in the pom.xml file to your local Maven repository. By doing so, it ensures that Maven has all the necessary resources locally, allowing you to build and work on the project without an internet connection. Once all necessary dependencies and plugins are downloaded (using a command like mvn dependency:go-offline), you can build and manage projects without needing an active internet connection. This command is especially useful in environments with limited or no internet access. It pre-fetches all dependencies and plugins required for the build process. After running this, you can work with Maven commands (like mvn compile or mvn package) without needing internet connectivity.

# Maven offers a wide range of commands to streamline Java project management. Here are some of the most commonly used ones:
#mvn compile: Compiles the source code of the project. This phase deletes the files generated during the previous builds, such as compiled classes, JAR files, and other output in the target directory. It's like starting with a clean slate to avoid potential issues caused by leftover files.
#mvn test: Runs the test cases included in the project.
#mvn clean: Deletes the target directory to clean up the project.
#mvn install: Installs the project artifact (e.g., JAR file) into the local Maven repository so it can be used as a dependency for other projects.
#mvn deploy: Deploys the artifact to a remote repository (e.g., Nexus or Artifactory) for distribution.
#mvn site: Generates a site or documentation for the project based on its pom.xml.
#mvn dependency:tree: Displays the project's dependency hierarchy to help debug dependency issues.
#mvn verify: Verifies that the project meets integration test and quality requirements.
#mvn package: Packages the compiled code into a distributable format, like a JAR or WAR file. This phase compiles the source code, runs tests (if configured), and packages the compiled code into its distributable format, such as a JAR or WAR file, depending on your project's configuration.
#mvn exec:java -Dexec.mainClass=[mainClassName]: Executes a Java program by running the specified main class.

# The java -cp command in Java is used to specify the classpath when running a Java application. The classpath is a parameter that tells the Java Virtual Machine (JVM) where to look for classes and packages needed to execute the program. Here's a breakdown: -cp or -classpath: This option is followed by a list of directories, JAR files, or ZIP files that the JVM will use to find required classes.
# Usage: java -cp [path-to-classpath] [class-name]
# Replace [path-to-classpath] with the directories or JAR files containing your compiled classes.
# Replace [class-name] with the name of the class that contains the main method (excluding the .class extension).
#
# java -cp .;lib/example.jar MainClass
# ENTRYPOINT ["java", "-cp", "/usr/app/multistagebuild-1.0-SNAPSHOT-jar-with-dependencies.jar", "com.dev.multistagebuild.App"]

# (-e JAVA_OPTS="-Xmx512m -Xms256m") is used to configure JVM-related options, CATALINA_OPTS (-e CATALINA_OPTS="-Dcatalina.http.port=8082") configure Tomcat server-related configurations.

# To stop stacktraces truncating in logs increase -XX:MaxJavaStackTraceDepth JVM option like: java -XX:MaxJavaStackTraceDepth=1000000

#You can check the RAM used by app running in a container by running:
#    docker stats containername
#To check CPU and memory configuration:
#    docker info | grep -iE "CPUs|Memory"
#You can verify how is the default Java heap size determined using:
#    java -XX:+PrintFlagsFinal -version | grep HeapSize
#You will get initial heap memory (like 256MiB) and a maximum heap size of (like 4GiB). The bare minimum you'll get away with is around 72M total memory on the simplest of Spring Boot applications with a single controller and embedded Tomcat. Throw in Spring Data REST, Spring Security and a few JPA entities and you'll be looking at 200M-300M minimum.
#To look at that heap size run:
#    $ java -XX:+PrintFlagsFinal -version | grep -Ei "maxheapsize|maxram"
#This outputs MaxHeapSize, MaxRAM, MaxRAMFraction, MaxRAMPercentage, SoftMaxHeapSize

#Buildpacks are a tool that provides framework and application dependencies. Spring Boot includes both Maven and Gradle support for buildpacks. For example, building with Maven, we would run the command: ./mvnw spring-boot:build-image
########################################################################################################################