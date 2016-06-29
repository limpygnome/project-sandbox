#!/usr/bin/env bash

(
    # Use Docker...
    cd components/website
    mvn clean package
    cd ../deploy
    ./create-cluster.sh
)
