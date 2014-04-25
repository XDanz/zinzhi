#!/bin/sh

. ../include.sh




cd node0
export CONFD_IPC_PORT=4565
${CONFD} -c ./confd.conf --stop
cd ..

cd node1
export CONFD_IPC_PORT=4575
${CONFD} -c ./confd.conf --stop
cd ..

cd node2
export CONFD_IPC_PORT=4585
${CONFD} -c ./confd.conf --stop
cd ..

