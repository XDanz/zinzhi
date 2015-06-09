#!/bin/bash

source ${DEV_SUPPORT_HOME}/env.sh

usage() 
{
    cat <<EOF
Usage: `basename $0` [nextgen|nextgen_pu|nextgen_market_pu]
    Build nextgen, nextgen_pu, nextgen_market_pu.
EOF
}

if [ $# -eq 0 ]; then
    usage 
    exit 65
fi

while [ $# -gt 0 ]; do
    case "$1" in
        nextgen)
            MUTE="$2"
            shift
            if [[ "${MUTE}" = "-v" ]]; then
                maven_build_nextgen verbose
            else
                maven_build_nextgen
             fi
            ;;
        nextgen_pu)
            shift
            MUTE="$1"
            if [[ "${MUTE}" = "-v" ]]; then
                maven_build_nextgen_pu verbose
            else
                maven_build_nextgen_pu
             fi
            ;;
        nextgen_market_pu)
            shift
            MUTE="$1"
            if [[ "${MUTE}" = "-v" ]]; then
                maven_build_market_pu verbose
            else
                maven_build_market_pu
             fi
            ;;
        *)
            usage
            exit 65
            ;;
    esac
    shift       # Check next set of parameters.
done
