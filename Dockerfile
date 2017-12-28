FROM openjdk:8

RUN mkdir -p /usr/src/potic-logger && mkdir -p /opt

COPY build/distributions/* /usr/src/potic-logger/

RUN unzip /usr/src/potic-logger/potic-logger-*.zip -d /opt/ && ln -s /opt/potic-logger-* /opt/potic-logger

WORKDIR /opt/potic-logger

EXPOSE 8080
ENV ENVIRONMENT_NAME test
ENTRYPOINT [ "sh", "-c", "./bin/potic-logger --spring.profiles.active=$ENVIRONMENT_NAME" ]
CMD []
