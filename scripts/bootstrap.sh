#!/usr/bin/env zsh
# bootstrap.sh: bootstrap our development environment

# Create the directory for storing the toolcahin
mkdir -p .toolchain
cd .toolchain

## Get the 2021.1.3 version of the IntelliJ IDEA Community
wget https://download.jetbrains.com/idea/ideaIC-2021.1.3.tar.gz
tar -xvf ideaIC-2021.1.3.tar.gz
rm *.tar.gz
mv idea-IC-211.7628.21 idea

## Install direnv and docker
# direnv: used to help make it easier to load the dependencies needed for
#         this project
#
# docker: used for running neuron for builds
# ffmpeg: For making screenrecordings
sudo apt install -y direnv docker ffmpeg
