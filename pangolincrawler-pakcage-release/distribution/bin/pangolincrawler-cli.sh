#!/bin/bash

# Control script for the pangolin crawler command line tools.

# determine pangolincrawler home
SCRIPT="$0"
PANGOLIN_HOME=`dirname "$SCRIPT"`/..

# make PANGOLIN_HOME absolute
PANGOLIN_HOME=`cd "$PANGOLIN_HOME"; pwd`

PANGOLIN_SERVER_NAME="Pangolin Crawler Server"

PC_CLASSPATH="$PANGOLIN_HOME/libs/cli/*"

# make PANGOLIN_HOME absolute
PANGOLIN_HOME=`cd "$PANGOLIN_HOME"; pwd`

PC_JAVA_OPTS="$PC_JAVA_OPTS -Dpangolin.path.home=$PANGOLIN_HOME"
PC_JAVA_OPTS="$PC_JAVA_OPTS -Duser.dir=$PANGOLIN_HOME"

if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_BIN="$JAVA_HOME/bin/java"
else
    JAVA_BIN=`which java`
fi

$JAVA_BIN $PC_JAVA_OPTS  -cp "$PC_CLASSPATH" org.pangolincrawler.cli.PangolinCliApp