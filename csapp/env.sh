#!/bin/bash

# source this file to get environment setup for the
# confd below here


function abspath() {
    if [[ -d "$1" ]]; then # if directory
        pushd "$1" >/dev/null
        pwd -P   # avoid all symlinks
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

# Trick so that we can source env.sh without
# being in the right directory


earg=${BASH_SOURCE:-$0}
echo "earg=$earg"
dirn=`dirname $earg`
echo "dirn=$dirn"
p=`abspath $dirn`
echo "p=$p"





# add_to_path () {
#     ## add element first in path, remove any other occurances
#     ## of the same further down the path
#     eval "origpath=\$$1"
#     origpath=`echo "$2:$origpath" | sed "s;:$2:;:;g"`
#     eval "export $1=$origpath"
# }
#

add_to_path () {
    eval "origpath=\$$1"

    newpath=$2:$origpath
    newpath=$(echo $newpath | awk 'BEGIN{ORS=":";RS=":"}!a[$0]++')

    eval "export $1=$newpath"
}

# ## webui npm
# add_to_path PATH ./node_modules/.bin

# ## pyang things, but not YANG_MODPATH
csapp_code=$p/code
echo "root=$code"
export CSAPP_DIR=$csapp_code

# add_to_path PATH ${pyang}/bin
# add_to_path PYTHONPATH ${pyang}

# ##
# add_to_path PATH ${p}/system/test/bin
# add_to_path PATH ${p}/otp/installed/bin
# add_to_path PATH ${p}/bin
# add_to_path MANPATH ${p}/confd_dir/man
# add_to_path MANPATH ${p}/ncs_dir/man

# ## python maapi src etc
# add_to_path PYTHONPATH  ${p}/ncs_dir/src/ncs/py_src

# export LD_LIBRARY_PATH=${p}/lib/capi/c_src/src
# export DYLD_LIBRARY_PATH=${p}/lib/capi/c_src/src
# export CONFD_DIR=${p}/confd_dir
# export NCS_DIR=${p}/ncs_dir
# export CONFDX_DIR=${p}
# export TEST_DIR=${p}/system/test
# export TAILF_BUILD=local
# export ERL_TOP=${p}/otp
# export W=${p}
# export LUX_SYSTEM_FLAGS="--config_dir ${p}/system/test/lux_config"
# export NCS_JAVADEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=9000,server=y,suspend=n"
# NCS_JAVAOPTS="-classpath ."
# for i in `find ${NCS_DIR}/java/jar/*.jar`; do \
#     if [ -h $i ]; then
#         NCS_JAVAOPTS="${NCS_JAVAOPTS}:$i"
#     fi
# done
# export NCS_JAVAOPTS
# export TAILFLOW_DIR=${p}/lib/tailflow

