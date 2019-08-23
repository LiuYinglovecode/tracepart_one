#!/bin/bash
# build uc product with directory input
# using new build framework to build
WORK_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
pushd $WORK_DIR 1>/dev/null 2>&1
rm -f oo.sh
if [ ! -f oo.sh ]; then
    curl https://repomaven.htres.cn/repository/yunlurepo/oo.sh > oo.sh
fi
source ./oo.sh
command -v import > /dev/null 2>&1  ||  { echo >&2 "need source oo.sh"; exit 1; }
#--------------------------------------------------------------
import util/buildFun
import util/log
namespace ucBuild
# init build env
PUBLISH_REG="registry.htres.cn"
#云路公司内部镜像仓库
INTERNAL_REG="registry.doc.htyunlu.com:5050"
# default publish to internal registry
REMOTE_REG=$INTERNAL_REG
IMAGE_ROOT=yunlu
# wechat api url with source in http://172.17.60.106/devteam/BDE/tree/V2/micro-service/wxmessage
WECHAT_API=http://registry.doc.htyunlu.com:8888/wechat/message
# default publish dir
#  mount -t cifs //172.17.60.104/uc-service_release /mnt/uc-service_release -o username=builder,password=1234.asd,uid=jenkins,gid=jenkins
PUBLISH_DIR=/data/app/appcockpit_release
PUBLISH_LATEST=latest
# generate build version
BUILD_VERSION=$(gen_tag_with_rev)

Log::AddOutput ucBuild BUILDLOG
Log::AddOutput error ERROR

if [[ -z "$PUBLISH_BUILD" ]]; then
    REMOTE_REG=$INTERNAL_REG
else
    REMOTE_REG=$PUBLISH_REG
fi
echo REMOTE_REG is $REMOTE_REG
#-------------------------------------------------------------------
# find all projects need to build
if [ $# -eq 0 ]
then
    echo "Usage:build_dir.sh [directory1 directory2 ...]"
    exit 1
fi

until [ $# -eq 0 ]
do
    unset PROJECT_DIR
    PROJECT_DIR=$1
    if [ ! -d "$PROJECT_DIR" ]; then
        echo $PROJECT_DIR is not exists !!!
        exit 1
    fi

    pushd $PROJECT_DIR 1>/dev/null 2>&1
    #------------------------------------
    if [ -f build.oo ]; then
      builtin source ./build.oo
    fi

    #------------------------------------
    if [ -f publish.oo ]; then
      builtin source ./publish.oo
    fi
    #------------------------------------
    popd 1>/dev/null 2>&1
    shift
done

#--------------------------------------------------------------
popd $WORK_DIR 1>/dev/null 2>&1
