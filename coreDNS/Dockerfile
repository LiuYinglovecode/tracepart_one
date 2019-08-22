FROM registry.htres.cn/yunlu/jdk8-alpine:latest

ENV RUN_PREFIX  /opt

ENV APP_NAME crawler-coredns-0.0.1-SNAPSHOT

RUN mkdir -p ${RUN_PREFIX}

EXPOSE 7021

WORKDIR ${RUN_PREFIX}/${APP_NAME}


ADD build/distributions/${APP_NAME}.tar /opt/

# profile represent different config for production or development or test purpose
ENV PROFILE_CONF dev

ENV ES_CLUSTER_ADDRESS http://127.0.0.1:9300

ENV ES_CLUSTER_NAME elasticsearch

CMD ["sh", "-c", "./bin/crawler-coredns -d ${PROFILE_CONF} -a ${ES_CLUSTER_ADDRESS} -n ${ES_CLUSTER_NAME}"]
