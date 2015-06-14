#! /bin/bash
### BEGIN INIT INFO
# Provides:          gigaspaces
# Required-Start:    $remote_fs
# Required-Stop:     $remote_fs
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: GigaSpaces XAP Premium
# Description:       Provides start/stop for GigaSpaces XAP Premium
### END INIT INFO

#. /etc/rc.d/init.d/functions

# Author: Jos Lagerweij <jlagerweij@tricode.nl>
#

# Do NOT "set -e"

#ulimit -n 655361

if [[ -f "${DEV_SUPPORT_HOME}/env.sh" ]]; then
    . ${DEV_SUPPORT_HOME}/env.sh
fi

# PATH should only include /usr/* if it runs after the mountnfs.sh script
#PATH=/sbin:/usr/sbin:/bin:/usr/bin
VSN=$(echo `basename $GIGASPACES_HOME` | sed 's/gigaspaces-xap-premium-//' | sed 's/-ga//')
DESC="GigaSpaces XAP Premium ${VSN}"
NAME=gigaspaces
<<<<<<< HEAD
echo $GIGASPACES_HOME
# For a GigaSpaces management use the line below
#DAEMON_ARGS="gsa.gsc 0 gsa.global.gsm 1 gsa.global.lus 1"
# For a GigaSpaces container use the line below
DAEMON_ARGS="gsa.gsc 0 gsa.gsm 1 gsa.global.gsm 0 gsa.lus 1 gsa.global.lus 0"

DEAMON_ARG_NUMBER_OF_GSCS=${NUMBER_OF_GSCS:=3}
DAEMON_ARGS_GSC="gsa.gsc ${DEAMON_ARG_NUMBER_OF_GSCS} gsa.gsm 0 gsa.global.gsm 0 gsa.lus 0 gsa.global.lus 0"
SCRIPTNAME=/etc/init.d/$NAME
LOGFILE1=/var/log/gigaspaces/$NAME.log

NIC_ADDR=127.0.0.1
echo "NIC_ADDR: $NIC_ADDR"
export NIC_ADDR

# Exit if the package is not installed
#[ -x "$DAEMON" ] || exit 0
[ -x "$DAEMON" ] || echo notinstalled

# Read configuration variable file if it is present
[ -r /etc/default/$NAME ] && . /etc/default/$NAME

#
# Function that starts the daemon/service
#
do_start()
{
	# Return
	#   0 if daemon has been started
	#   1 if daemon was already running
	#   2 if daemon could not be started

	pushd $GIGASPACES_HOME

	GS_START_LINE="$DAEMON $DAEMON_ARGS"
	echo "Starting GigaSpaces"
	$GS_START_LINE > $LOGFILE1 2>&1 &

	GSC_JAVA_OPTIONS_BASE=${GSC_JAVA_OPTIONS}
        echo "Using JAVA_OPTIONS_BASE to \"${GSC_JAVA_OPTIONS_BASE}\""

	#export GSC_JAVA_OPTIONS="$GSC_JAVA_OPTIONS_BASE -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"
	export GSC_JAVA_OPTIONS="$GSC_JAVA_OPTIONS_BASE "

	GS_START_LINE="$DAEMON $DAEMON_ARGS_GSC"
	echo "Starting GS with:"
	echo $GS_START_LINE
	$GS_START_LINE >> $LOGFILE1 2>&1 &

	# Add code here, if necessary, that waits for the process to be ready
	# to handle requests from services started subsequently which depend
	# on this one.  As a last resort, sleep for some time.

	sleep 5
	do_status
	popd
}

#
# Function that stops the daemon/service for this lookup group
#
do_stop()
{
	# Return
	#   0 if daemon has been stopped
	#   1 if daemon was already stopped
	#   2 if daemon could not be stopped
	#   other if a failure occurred
	
	PIDS=`ps -eo pgid,args | grep gigaspaces | egrep 'GSA|GSC|GSM|LH' | grep -v java | awk '{print $1}' | wc -l`
	if [ $PIDS -gt 0 ];
	  then
		do_status
		echo "Stopping $PIDS GigaSpaces processes."
		ps -eo pgid,args | grep gigaspaces | grep GSA | grep java | awk '{print $1}' | xargs -I {} kill -s TERM -- -{}
		RETVAL=0
	  else
		echo "No GigaSpaces processes to stop."
		RETVAL=1
	fi

	return "$RETVAL"
}

#
# Function that stops the daemon/service for this lookup group
#
do_terminate()
{
	# Return
	#   0 if daemon has been stopped
	#   1 if daemon was already stopped
	#   2 if daemon could not be stopped
	#   other if a failure occurred
	
	PIDS=`ps -eo pgid,args | grep gigaspaces | egrep 'GSA|GSC|GSM|LH' | grep -v java | awk '{print $1}' | wc -l`
	if [ $PIDS -gt 0 ];
	  then
		do_status
		echo "Terminating $PIDS GigaSpaces processes."
		ps -eo pgid,args | grep gigaspaces | grep GSA | grep java | awk '{print $1}' | xargs -I {} kill -s KILL -- -{}
		RETVAL=0
	  else
		echo "No GigaSpaces processes to terminate."
		RETVAL=1
	fi

	return "$RETVAL"
}

#
# Function that shows the number of running GigaSpaces instances for this lookup group
#
do_status() {
	PIDS_GSA=`ps -eo pgid,args | grep gigaspaces | grep GSA | grep -v java | wc -l`
	PIDS_GSM=`ps -eo pgid,args | grep gigaspaces | grep GSM | grep -v java | wc -l`
	PIDS_GSC=`ps -eo pgid,args | grep gigaspaces | grep GSC | grep -v java | wc -l`
	PIDS_LH=`ps -eo pgid,args | grep gigaspaces | grep LH | grep -v java | wc -l`
	PIDS=$(($PIDS_GSA + $PIDS_GSM + $PIDS_GSC + $PIDS_LH))

	echo "There are $PIDS GigaSpaces processes. $PIDS_GSA GSAs, $PIDS_GSM GSMs, $PIDS_GSC GSCs and $PIDS_LH LookupServices"
        if [ $PIDS -gt 0 ]; then
                return 0        # exit code 0 = program is running or service is OK
        else
                return 3        # exit code 3 = program is not running
        fi

	return 0
}

case "$1" in
  start)
	[ "$VERBOSE" != no ] && echo "Starting $DESC" "$NAME"
	do_start
	case "$?" in
		0|1) [ "$VERBOSE" != no ] && echo OK ;;
		2) [ "$VERBOSE" != no ] && echo FAILURE  ;;
	esac
	;;
  stop)
	[ "$VERBOSE" != no ] && echo "Stopping $DESC" "$NAME"
	do_stop
	case "$?" in
		0|1) [ "$VERBOSE" != no ] && echo OK ;;
		2) [ "$VERBOSE" != no ] && echo FAILURE ;;
	esac
	;;
  terminate)
	[ "$VERBOSE" != no ] && echo "Terminating $DESC" "$NAME"
	do_terminate
	case "$?" in
		0|1) [ "$VERBOSE" != no ] && echo OK ;;
		2) [ "$VERBOSE" != no ] && echo FAILURE ;;
	esac
	;;
  restart|force-reload)
	#
	# If the "reload" option is implemented then remove the
	# 'force-reload' alias
	#
	echo "Restarting $DESC" "$NAME"
	do_stop
	case "$?" in
	  0|1)
		do_start
		case "$?" in
			0) echo OK ;;
			1) echo FAILURE ;; # Old process is still running
			*) echo FAILURE ;; # Failed to start
		esac
		;;
	  *)
	  	# Failed to stop
		echo FAILURE
		;;
	esac
	;;
  status)
	do_status
	;;
  *)
	#echo "Usage: $SCRIPTNAME {start|stop|restart|reload|force-reload}" >&2
	echo "Usage: $SCRIPTNAME {start|stop|restart|status}" >&2
	exit 3
	;;
esac

