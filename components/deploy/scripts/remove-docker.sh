#!/usr/bin/env bash

# Stop daemon


# Wipe packages
sudo apt-get purge docker-engine

sudo apt-get autoremove --purge docker-engine
