#!/usr/bin/env bash

cd notes
mkdir -p .neuron/output && touch .neuron/output/.nojekyll
docker run -v $PWD:/notes sridca/neuron neuron gen
