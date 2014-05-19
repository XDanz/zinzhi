#! /bin/bash

vip=$1
masksz=$2
iface=$3
count=$4
removed=NO
do_at_exit() {
    if [ x${removed} = xNO ]; then
        echo "Autobring VIP ${vip}/${masksz} dev ${iface}:svip${count} down"
        sudo ip addr del ${vip}/${masksz} dev ${iface}:svip${count}
        removed=YES
     fi
    exit 0
}
trap do_at_exit 0 1 2 3 15
read
exit 0




