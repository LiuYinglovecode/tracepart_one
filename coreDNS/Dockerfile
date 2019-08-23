FROM registry.htres.cn/yunlu/jdk8-alpine:latest

ENV RUN_PREFIX  /opt

ENV APP_NAME coreDNS-0.0.1-SNAPSHOT

RUN mkdir -p ${RUN_PREFIX}

EXPOSE 7021

WORKDIR ${RUN_PREFIX}/${APP_NAME}


ADD build/distributions/${APP_NAME}.tar /opt/

CMD ["sh", "-c", "./bin/coreDNS-0.0.1-SNAPSHOT"]
