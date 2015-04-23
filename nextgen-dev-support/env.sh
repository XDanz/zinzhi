#!/bin/bash

function abspath() {
    if [[ -d "$1" ]]; then
        pushd "$1" >/dev/null
        pwd -P
        popd >/dev/null
    elif [[ -e $1 ]]; then
        pushd $(dirname $1) >/dev/null
        echo `pwd -P`/$(basename $1)
        popd >/dev/null
    else
        echo $1 does not exist! >&2
        return 127
    fi
}

earg=${BASH_SOURCE:-$0}
dirn=`dirname $earg`
p=`abspath $dirn`
export DEV_SUPPORT_HOME=$p
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export ANT_HOME=/usr/share/ant

if [[ -z "${GIGASPACES_HOME}" ]]; then
    export GIGASPACES_HOME=/home/danter/gigaspaces-xap-premium-9.7.1-ga
fi

if [[ -z "${NEXTGEN_HOME}" ]]; then 
    export NEXTGEN_HOME=/home/danter/dev/nextgen
fi

export JSHOMEDIR=$GIGASPACES_HOME

PATH=$PATH:$JAVA_HOME/bin
PATH=$PATH:$ANT_HOME/bin
PATH=$PATH:$M2_HOME/bin
PATH=$PATH:$GIGASPACES_HOME/bin
PATH=$PATH:$CLOUDIFY_HOME/bin

if [[ -z "${LOOKUPLOCATORS}" ]]; then
    export LOOKUPLOCATORS=localhost:4174
fi

if [[ -z "${LOOKUPGROUPS}" ]]; then
    export LOOKUPGROUPS=nextgen
fi

export GSA_JAVA_OPTIONS="-Xmx128m"
export GSM_JAVA_OPTIONS="-Xmx128m"
export GSC_JAVA_OPTIONS="-Xmx6g -Xms256m -XX:MaxPermSize=1g"

export NUMBER_OF_GSCS=2
echo "$DEV_SUPPORT_HOME"
source ${DEV_SUPPORT_HOME}/util/functions.sh
