FROM openjdk:8-jdk-alpine3.9
MAINTAINER devops "devops@tradinggroup.io"
ENV KAFKA_OPTS -Djava.security.auth.login.config=/home/devops/users_jaas.conf
RUN \
    apk update \
    && apk add stunnel busybox-suid busybox-extras curl \
    && chmod u+s /usr/bin/stunnel \
    && chmod 4755 /bin/busybox \
    && addgroup -g 1000 -S devops \
    && adduser devops -D -G devops -u 1000 -s /bin/sh \
    && echo "root:<CHANGE_ROOT_PASSW>" |chpasswd \
    && mkdir -p /home/devops/ssl/ \
    && cp -a /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts /home/devops/ssl/kafka.client.truststore.jks
ADD . /home/devops/
RUN chown -R devops:devops /home/devops/
WORKDIR  /home/devops
USER devops
EXPOSE  8010 8020 9999
CMD ["sh", "-c", "stunnel /home/devops/stunnel-redis-cli.conf;java ${KAFKA_OPTS} ${JVM_OPTIONS} ${JAVAAGENT_OPTIONS} -jar -server /home/devops/coupon-service-1.0-SNAPSHOT.jar --spring.profiles.active=${APP_ENV}"]
