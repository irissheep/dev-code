FROM bladex/alpine-java:openjdk8-openj9_cn_slim


RUN mkdir -p /blade

WORKDIR /blade

EXPOSE 8888

ADD ./target/blade-api.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

CMD ["--spring.profiles.active=dev"]
