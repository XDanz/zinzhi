#!/bin/bash
#
# Initialization script to be run by Vagrant inside the virtual machine.

echo "Installing ubuntu packages."
apt-get -q -y install \
 autoconf bison flex libtool xsltproc ant default-jdk \
 libpam0g-dev redir unzip emacs xterm python-paramiko libxml2-utils \
 libncurses5-dev libpam0g-dev libssl-dev openssh-server curl \
 python-libxml2 snmp-mibs-downloader \
 openssl erlang-dev erlang-src erlang-eunit erlang-edoc \
 screen erlang-mode \
 wget curl w3m rinetd




#
# Copy all scripts the VM will need later into /var/confd-vm-scripts/
#

ls /confd
mkdir -p tailf-src/trunk || true
cd tailf-src/trunk






