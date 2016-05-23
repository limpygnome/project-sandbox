#!/usr/bin/env bash

(
    mvn --projects components/shared,components/server clean package &&
    cd ./components/server/target &&
    java -jar `ls projectsandbox-*.jar`
)
