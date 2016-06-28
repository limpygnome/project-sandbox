#!/usr/bin/env bash

# Doesnt install daemon:
wget -qO- https://get.docker.com/ | sh

# Start service
sudo service docker start

#sudo apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
#
#sudo deb https://apt.dockerproject.org/repo ubuntu-trusty main
#sudo apt-get update
#sudo apt-get purge lxc-docker
#sudo apt-get install linux-image-extra-$(uname -r)
#
#sudo apt-get install docker-engine
