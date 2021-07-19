#!/usr/bin/env bash

cd notes
mkdir -p .neuron/output && touch .neuron/output/.nojekyll
docker run --rm -t -i -p 8080:8080 -v $(pwd):/notes sridca/neuron neuron gen -ws 0.0.0.0:8080
