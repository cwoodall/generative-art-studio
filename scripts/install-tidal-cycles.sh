#!/usr/bin/env bash

# Instructions from https://tidalcycles.org/docs/getting-started/linux_install/

sudo apt update
sudo apt install -y build-essential cabal-install git jackd2

# https://github.com/lvm/build-supercollider
cd .toolchain

git clone https://github.com/lvm/build-supercollider
cd build-supercollider
sh build-supercollider.sh
sh build-sc3-plugins.sh

cd ..

sclang install-superdirt.sc

cabal update
cabal install tidal

## Setup user into the realtime audio group
sudo usermod -a -G audio cwoodall