#!/bin/bash

bindir=$(cd $(dirname ${BASH_SOURCE[0]}); pwd)

for file in $(ls -1 ${bindir}/target/*.jar)
do
  if [ "$CLASSPATH" = "" ]
  then
    CLASSPATH=${file}
  else
    CLASSPATH=$CLASSPATH:${file}
  fi
done

export CLASSPATH

java ${1+$@}
