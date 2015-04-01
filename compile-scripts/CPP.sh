#!/bin/bash

SOURCE=$1
OUT=$2
COMOUT=$3
COMERROR=$4

g++ -static -fno-strict-aliasing -lm -x c++ -std=c++11 -O2 -o ${OUT} ${SOURCE} 1> ${COMOUT} 2> ${COMERROR}
