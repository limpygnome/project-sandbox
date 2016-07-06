#!/usr/bin/env bash

(
    # Use Docker...
    cd components/website
    mvn clean compile war:war -DskipTests
    cd ../deploy
    ./create-cluster.sh
)
