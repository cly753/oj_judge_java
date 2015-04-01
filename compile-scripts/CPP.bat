echo off

REM SOURCE=$1
REM OUT=$2
REM COMOUT=$3
REM COMERROR=$4

g++ -static -fno-strict-aliasing -lm -x c++ -std=c++11 -O2 -o %2 %1 1> %3 2> %4