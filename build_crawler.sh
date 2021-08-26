#!/bin/bash
# build coredns release
WORK_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
pushd $WORK_DIR 1>/dev/null 2>&1
rm -f oo.sh
if [ ! -f oo.sh ]; then
    #curl http://172.17.60.107:8081/repository/yunlurepo/oo.sh > oo.sh
    curl https://repomaven.htres.cn/repository/yunlurepo/oo.sh > oo.sh
fi
source ./oo.sh
command -v import > /dev/null 2>&1  ||  { echo >&2 "need source oo.sh"; exit 1; }
#--------------------------------------------------------------
import util/buildFun
import util/log
namespace corednsbuild
# init build env
#PUBLISH_REG="registry.htres.cn"
PUBLISH_REG="hub.htres.cn/pub"
DOCKER_USER=admin
DOCKER_PASS=XgE+CC5Vyo2n
DOCKER_HOST=hub.htres.cn
#云路公司内部镜像仓库
INTERNAL_REG="registry.htres.cn"
# default publish to internal registry
REMOTE_REG=$INTERNAL_REG
IMAGE_ROOT=yunlu
# wechat api url with source in http://172.17.60.106/devteam/coredns/tree/V2/micro-service/wxmessage
WECHAT_API=http://registry.doc.htyunlu.com:8888/wechat/message
# default publish dir
#  mount -t cifs //172.17.60.104/uc-service_release /mnt/uc-service_release -o username=builder,password=1234.asd,uid=jenkins,gid=jenkins
PUBLISH_DIR=/data/app/coredns_release
PUBLISH_LATEST=latest
# generate build version
BUILD_VERSION=$(gen_tag_with_rev)

Log::AddOutput corednsbuild BUILDLOG
Log::AddOutput error ERROR

if [[ -z "$PUBLISH_BUILD" ]]; then
    REMOTE_REG=$INTERNAL_REG
else
    REMOTE_REG=$PUBLISH_REG
    docker login --username=$DOCKER_USER --password=$DOCKER_PASS $DOCKER_HOST
fi
echo REMOTE_REG is $REMOTE_REG
#-------------------------------------------------------------------
# find all projects need to build
declare -a JOBS
JOBS=($(find . -name "build.oo"))
for JOB in "${JOBS[@]}"; do
    unset PROJECT_DIR
    PROJECT_DIR=$( cd "$( dirname "${JOB}" )" && pwd )
    pushd $PROJECT_DIR 1>/dev/null 2>&1
    #------------------------------------
    builtin source ./build.oo
    #------------------------------------
    popd 1>/dev/null 2>&1
done

if [[ -z "$PUBLISH_BUILD" ]]; then
    REMOTE_REG=$INTERNAL_REG
else
    REMOTE_REG=$PUBLISH_REG
fi
echo REMOTE_REG is $REMOTE_REG
unset JOBS
JOBS=($(find . -name "publish.oo"))
for JOB in "${JOBS[@]}"; do
    unset PROJECT_DIR
    PROJECT_DIR=$( cd "$( dirname "${JOB}" )" && pwd )
    pushd $PROJECT_DIR 1>/dev/null 2>&1
    #------------------------------------
    builtin source ./publish.oo
    #------------------------------------
    popd 1>/dev/null 2>&1
done

#--------------------------------------------------------------
popd $WORK_DIR 1>/dev/null 2>&1
