# generative-art-studio
Where all of my generative art experiments live

## Setup

### Kotlin and OPENRNDR 

#### Using IntelliJ IDEA

Using Ubuntu/Debian based linux:

1. Bootstrap the toolchain (download intellij and load it into the .toolchain folder): `./scripts/bootstrap`
2. Launch it: `./scripts/start_idea.sh`


#### Using SDKMAN (Deprecated)
- need `git`
- snap
  - sudo apt install -y snapd
- kotlin 
  - using `sdkman`
    - https://sdkman.io/
  - sdk install kotlin
  - sdk install gradle


[Openrndr guide](https://guide.openrndr.org/#/02_Getting_Started_with_OPENRNDR/C00_SetupYourFirstProgram)


## Generating the Neuron Page

Requires docker, otherwise uses the docker method. See `.github/workflows/publish-website.yaml` for details. 

```bash
cd notes
mkdir -p .neuron/output && touch .neuron/output/.nojekyll
docker run -v $PWD:/notes sridca/neuron neuron gen
```

To run a live demo

```bash
cd notes
mkdir -p .neuron/output && touch .neuron/output/.nojekyll
docker run --rm -t -i -p 8080:8080 -v $(pwd):/notes sridca/neuron neuron gen -ws 0.0.0.0:8080
```