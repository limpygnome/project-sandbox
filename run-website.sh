#!/usr/bin/env bash

(
    # Use Docker...
    mvn clean package
    cd components/deploy
    ./create-cluster.sh
)
