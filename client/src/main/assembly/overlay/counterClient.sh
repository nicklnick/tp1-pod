#!/usr/bin/bash

MAIN_CLASS="ar.edu.itba.pod.grpc.client.clients.CounterClient"

java $* -cp 'lib/jars/*' $MAIN_CLASS