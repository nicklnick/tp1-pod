#!/usr/bin/bash

MAIN_CLASS="ar.edu.itba.pod.grpc.client.clients.PassengerClient"

java $* -cp 'lib/jars/*' $MAIN_CLASS