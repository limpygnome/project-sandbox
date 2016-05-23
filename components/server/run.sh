#!/usr/bin/env bash

(
    cd ../../ &&
    mvn clean package &&
    cd ./components/server/target &&
    java -jar `ls *.jar`
)
