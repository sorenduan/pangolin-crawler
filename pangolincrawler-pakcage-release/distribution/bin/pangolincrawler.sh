#!/bin/bash

# Control Script for the Pangolin Crawler Server
#
# Environment Variable Prerequisites
#
#   Do not set the variables in this script. Instead put them into a script
#   setenv.sh in PANGOLIN_HOME/bin to keep your customizations separate.
#
#   PANGOLIN_HOME   May point at your Pangolin Crawler "build" directory.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#                   Required to run the with the "debug" argument.
#
#   PC_JAVA_OPTS    (Optional) Java runtime options used when any command
#                   is executed.
#
#                   PC_JAVA_OPTS="-Xms8g -Xmx8g" ./bin/pangolincrawler
#
#   PC_CLASSPATH    A Java classpath containing everything necessary to run.



SCRIPT="$0"

PANGOLIN_SERVER_PORT="9797"

# determine pangolincrawler home
PANGOLIN_HOME=`dirname "$SCRIPT"`/..

# make PANGOLIN_HOME absolute
PANGOLIN_HOME=`cd "$PANGOLIN_HOME"; pwd`

PANGOLIN_SERVER_NAME="Pangolin Crawler Server"


if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_BIN="$JAVA_HOME/bin/java"
else
    JAVA_BIN=`which java`
fi


PC_CLASSPATH="$PANGOLIN_HOME/libs/server/*"

PC_JAVA_OPTS="$PC_JAVA_OPTS -Dpangolin.path.home=$PANGOLIN_HOME"
PC_JAVA_OPTS="$PC_JAVA_OPTS -Duser.dir=$PANGOLIN_HOME"

PID_FILE=$PANGOLIN_HOME/pangolin.pid

ARGS_LINE="$*"

function help()
{

    echo "Usage: $0 {start|stop|restart|version}"
    echo "commands:"
    echo "  start         Start $PANGOLIN_SERVER_NAME in the current console."
    echo "  start -d|--daemonize"
    echo "                Start $PANGOLIN_SERVER_NAME in the background."
    echo "  stop          Stop $PANGOLIN_SERVER_NAME, waiting up to 5 seconds for the process to end"

    RETVAL="2"
}


function version(){
    RETVAL="2"
}


function start()
{
    PID=$( getpid )
    
    if [ "$PID" != "" ]; then
        echo "The pangolin server is already running, the pid is $PID"
        exit 1
    fi
    
    # check the jvm version , requires at least Java 8
    $JAVA_BIN $PC_JAVA_OPTS -cp "$PC_CLASSPATH" org.pangolincrawler.core.tools.JvmVersionChecker

    if [ $? -ne 0 ]; then
        echo "Pangolin Crawler requires at least Java 8 but your Java version from $JAVA_BIN does not meet this requirement"
        exit 1
    fi

    daemonized=`echo $ARGS_LINE | egrep -- '(^-d |-d$| -d |--daemonize$|--daemonize )'`

    if [ -z "$daemonized" ] ; then
        $JAVA_BIN $PC_JAVA_OPTS -cp "$PC_CLASSPATH" \
                org.pangolincrawler.core.PangolinApplication
    else
        echo -n "Starting $PANGOLIN_SERVER_NAME:    "
#        $JAVA_BIN $PC_JAVA_OPTS -cp "$PC_CLASSPATH" \
#                org.pangolincrawler.core.PangolinApplication  <&- &
        $JAVA_BIN $PC_JAVA_OPTS -cp "$PC_CLASSPATH" \
                org.pangolincrawler.core.PangolinApplication > /dev/null &
        PID=$!
        if [ $? -ne 0 ]; then
            exit $?
        fi
        echo $PID > $PID_FILE
        # waiting for start success.
        for i in {1..60}
        do
            echo -ne '\b'
            if [ $i -gt 9 ]; then
                echo -ne '\b'
            fi
            
            if [ $i -gt 99 ]; then
                echo -ne '\b'
            fi
            
            echo -ne "$i"
            sleep 1
            OUTPUT=$( lsof -i:$PANGOLIN_SERVER_PORT | grep $PID )
            if [ $? -eq 0 ]; then
                echo -ne '\b\b\b'
                echo -n "  Success."
                echo ""
                exit 0;
            fi
        done
        echo -n "Fail."
        echo ""
        exit 1
    fi
}

function getpid()
{
    JPS_BIN=`which jps`
    if [ "$JPS_BIN" != "" ]; then
        PID=$(jps -l | grep 'org.pangolincrawler.core.PangolinApplication' | awk '{print $1}')
    else
        PID=$(ps -e | grep 'org.pangolincrawler.core.PangolinApplication' | awk '{print $1}')
    fi
    echo $PID
}

function stop()
{
    echo -n Stopping $PANGOLIN_SERVER_NAME:

    if [ -f $PID_FILE ]; then
        PID=`cat ${PID_FILE}`
    else
        PID=$( getpid )
    fi
    
    if [ "$PID" != "" ]; then
        kill $PID
        for i in {1..60}
        do
            echo -n "."
            sleep 1
            ps -p $PID > /dev/null
        
            if [ $? -ne 0 ]; then
                if [ -f $PID_FILE ]; then
                    rm $PID_FILE
                fi
                echo "OK"
                exit 0
            fi
        done
        echo "Stop $PANGOLIN_SERVER_NAME time out, try to force kill progress."
        kill -9 $PID
    fi

    echo "Stop pangolin server fail, can not find the pid file ($PID_FILE) or the pangolin processs. "
    exit 1
}


case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    *)
      help
      ;;
esac

exit $RETVAL