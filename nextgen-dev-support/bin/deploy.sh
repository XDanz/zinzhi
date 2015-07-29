#!/bin/bash

source ${DEV_SUPPORT_HOME}/env.sh

usage() 
{
    cat <<EOF
Usage: `basename $0` nextgen|market|nordea

    Deploy (nextgen pu) or market (nextgen market pu), nordea (nordea pu).
EOF
}

if [ $# -eq 0 ]; then
    usage 
    exit 65
fi

while [ $# -gt 0 ]; do
    case "$1" in
        nextgen)
            maven_deploy_nextgen
            ;;
        omx)
            maven_deploy_omx
            ;;
        nordea)
            maven_deploy_nordea
            ;;
        ngm)
            maven_deploy_ngm
            ;;

    esac
    shift
done
