#!/usr/bin/env bash

(
    mvn clean package &&
    cd ./components/server/target &&
    java -jar `ls *.jar`
)
