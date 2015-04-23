#!/bin/bash

runpath=$(dirname $0)/..
source $runpath/env.sh
pushd "${NEXTGEN_HOME}" > /dev/null
maven_build quite
popd >/dev/null
