#!/usr/bin/env bash

(
    # Use Docker...
    cd components/website
    mvn clean compile war:exploded -DskipTests
    cd ../deploy
    ./create-cluster.sh
)
