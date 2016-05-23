#!/usr/bin/env bash


(
    export MAVEN_OPTS=-agentlib:jdwp=transport=dt_socket,address=8081,server=y,suspend=n &&
    mvn clean tomcat7:run
)
