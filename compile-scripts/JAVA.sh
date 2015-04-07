#!/usr/bin/env bash

SOURCE=$1
OUT=$2
COMOUT=$3
COMERROR=$4

javac -g:none -source 7 -s ${OUT} -target 7 -Xlint:none -Xstdout ${COMOUT} ${SOURCE}
