@echo off

REM EXE=$1
REM INFILE=$2
REM OUTFILE=$3
REM ERRORFILE=$4
REM TIMEOUT=$5

Powershell.exe -executionpolicy RemoteSigned -Command "Measure-Command { %1 0< %2 1> %3 2> %4 } > %5"