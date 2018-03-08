#!/usr/bin/env sh

##############################################################################
##
##  Stop and kill currently running docker image, pull newest version and
##  run it.
##
##############################################################################

warn ( ) {
    echo "$*"
}

warn "Currently running docker images"
docker ps -a

warn "Killing currently running docker image..."
docker kill potic-logger; docker rm potic-logger

warn "Pulling latest docker image..."
docker pull potic/potic-logger:$TAG_TO_DEPLOY

warn "Starting docker image..."
docker run -dit --name potic-logger --restart on-failure --link potic-users -e LOG_PATH=/mnt/logs -v /mnt/logs:/mnt/logs -e LOGZIO_TOKEN=$LOGZIO_TOKEN -p 40410:8080 potic/potic-logger:$TAG_TO_DEPLOY

warn "Currently running docker images"
docker ps -a
