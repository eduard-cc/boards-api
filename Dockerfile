FROM gradle:8.3-jdk17
WORKDIR /opt/app
COPY ./build/libs/Boards-0.0.1-SNAPSHOT.jar ./

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar Boards-0.0.1-SNAPSHOT.jar"]