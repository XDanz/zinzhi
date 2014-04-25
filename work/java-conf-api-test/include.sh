CONFD=${CONFD_DIR}/bin/confd
CONFDC=${CONFD_DIR}/bin/confdc
CONFDCLI=${CONFD_DIR}/bin/confd_cli

JARFILE=${CONFD_DIR}/java/jar/conf-api-4.0.0.jar


LOG4J=${CONFD_DIR}/java/jar/log4j-1.2.16.jar
JUNIT=${CONFD_DIR}/java/jar/junit-4.8.2.jar

CLASSPATH=${JARFILE}:${LOG4J}:${JUNIT}:.


JAVAC=javac
JAVA=java
NETCONF_CONSOLE_TCP=${CONFD_DIR}/bin/netconf-console-tcp
CDB_DIR=./confd-cdb
