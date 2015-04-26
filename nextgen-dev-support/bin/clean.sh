#!/bin/bash

databases="system-mirror esb-mirror party-mirror reference-mirror account-mirror transaction-mirror \
price-mirror omx-tgw-inet-mirror omx-mdi-price-mirror audit-mirror execution-mirror nordea-tgw-mirror \
nordea-mdi-mirror"

source ${DEV_SUPPORT_HOME}/env.sh

for dbs in $databases
do
   mongo $dbs --eval "db.dropDatabase()" > /dev/null || { echo "Failed to clean db!" >&2 ; exit 1; }
done

clean_deploy_dir
# mongo esb-mirror --eval "db.dropDatabase()"
# mongo party-mirror --eval "db.dropDatabase()"
# mongo reference-mirror --eval "db.dropDatabase()"
# mongo account-mirror --eval "db.dropDatabase()"
# mongo transaction-mirror --eval "db.dropDatabase()"
# mongo price-mirror --eval "db.dropDatabase()"
# mongo omx-tgw-inet-mirror --eval "db.dropDatabase()"
# mongo omx-mdi-price-mirror --eval "db.dropDatabase()"
# mongo audit-mirror --eval "db.dropDatabase()"
# mongo execution-mirror --eval "db.dropDatabase()"
# mongo nordea-tgw-mirror --eval "db.dropDatabase()"
# mongo nordea-mdi-mirror --eval "db.dropDatabase()"
