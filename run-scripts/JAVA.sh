#!/usr/bin/env bash

EXE=$1
INFILE=$2
OUTFILE=$3
ERRORFILE=$4
METRICSFILE=$5
TIMELIMIT=$6 # ms
MEMORYLIMIT=$7 # KBytes
MAXFILE=$8
MAXTIME=$9
MAXMEMORY=${10}

cd ${EXE}

ulimit -f ${MAXFILE}
# ulimit -v ${MAXMEMORY}

#
# TODO
# record ulimit violation
# http://stackoverflow.com/questions/3043709/resident-set-size-rss-limit-has-no-effect/6365534#6365534
#

# /usr/bin/time -o ${METRICSFILE} -f "%U\n%M" ${EXE} 0< ${INFILE} 1> ${OUTFILE} 2> ${ERRORFILE}

# java -client -Djava.security.manager -Xmx${MEMORYLIMIT}k -Xss64m Main 0< ${INFILE} 1> ${OUTFILE} 2> ${ERRORFILE}

/usr/bin/time -o ${METRICSFILE} -f "%U\n-1" java -client -Djava.security.manager -Xmx${MEMORYLIMIT}k -Xss64m Main 0< ${INFILE} 1> ${OUTFILE} 2> ${ERRORFILE}