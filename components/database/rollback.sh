#!/bin/bash

PARAM=$1

if [[ "${PARAM}" == "force" ]]; then
	mvn clean liquibase:clearCheckSums
fi

mvn clean liquibase:rollback -Dliquibase.rollbackCount=1
