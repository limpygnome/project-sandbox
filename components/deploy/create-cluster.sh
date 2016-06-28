#!/usr/bin/env bash
### Tested using Docker 1.9.1

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Make sure log dirs exist
mkdir -p "${DIR}/logs/web"

# Build image
echo "Building ps-tomcat-base image..."
sudo docker build -t "ps-tomcat-base" --file "${DIR}/ps-tomcat-base/dockerfile" ${DIR}

# Start cluster
echo "Starting cluster..."
CLUSTER_YAML_FILE="${DIR}/cluster.yaml"

sudo docker-compose -f "${CLUSTER_YAML_FILE}" up -d

# List running nodes
sudo docker ps

# Wait for key to kill cluster...
echo -e "\n\nPress any key to destroy cluster..."
read -n1 -r

sudo docker-compose -f "${CLUSTER_YAML_FILE}" stop
echo "y" | sudo docker-compose -f "${CLUSTER_YAML_FILE}" rm
