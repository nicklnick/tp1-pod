#!/usr/bin/bash

PATH_TO_CODE_BASE=`pwd`

MAIN_CLASS="ar.edu.itba.pod.grpc.server.Server"

PORT_NUMBER="50051"

while getopts ":D:" opt; do
  case ${opt} in
    D )
      if [[ $OPTARG == port=* ]]; then
        PORT_NUMBER="${OPTARG#*=}"
      fi
      ;;
    \? )
      echo "Invalid option: $OPTARG" 1>&2
      ;;
  esac

done
shift $((OPTIND -1))

java -Dport=$PORT_NUMBER -cp 'lib/jars/*' $MAIN_CLASS $*
