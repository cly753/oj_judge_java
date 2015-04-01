#!/bin/bash

EXE=$1
INFILE=$2
OUTFILE=$3
ERRORFILE=$4
TIMEOUT=$5

"" > ${OUTPATH}/exeOutput
"" > ${OUTPATH}/exeError

{ time ${EXE} 0< ${INFILE} 1> ${OUTFILE} 2> ${ERRORFILE} ; } 2> ${TIMEOUT}
