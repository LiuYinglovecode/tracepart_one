FROM registry.htres.cn/yunlu/jdk8-alpine:latest

ENV RUN_PREFIX  /opt

ENV APP_NAME coreDNS-0.0.1-SNAPSHOT

RUN mkdir -p ${RUN_PREFIX}
ADD build/distributions/${APP_NAME}.tar /opt/

EXPOSE 7021

WORKDIR ${RUN_PREFIX}/${APP_NAME}


ENV PROFILE_CONF test

CMD ["sh", "-c", "./bin/coreDNS -d ${PROFILE_CONF}"]
