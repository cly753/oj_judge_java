#!/bin/bash

# memory (kbytes)

EXE=$1
INFILE=$2
OUTFILE=$3
ERRORFILE=$4
METRICSFILE=$5
MAXFILE=$6
MAXTIME=$7
MAXMEMORY=$8

ulimit -f ${MAXFILE}
ulimit -m ${MAXMEMORY}

#
# TODO
# record ulimit violation
# 

/usr/bin/time -o ${METRICSFILE} -f "%U\n%M" ${EXE} 0< ${INFILE} 1> ${OUTFILE} 2> ${ERRORFILE}