# 航天云路 Java 爬虫镜像
#
# VERSION 0.1
# java8 jdk
FROM java:openjdk-8-jre-alpine

# user 使用 root 权限
USER root

# 设置工作目录为 /mydir
WORKDIR /mydir

# 复制本地文件到工作目录
COPY build/libs/spider-1.0-SNAPSHOT.jar /mydir

# 默认的启动命令
CMD ["java" ,"-cp", "spider-1.0-SNAPSHOT.jar ${PACKAGE}"]
#运行命令：docker run f1d1ae72 java -cp spider-1.0-SNAPSHOT.jar spider.company.atoboCompany
