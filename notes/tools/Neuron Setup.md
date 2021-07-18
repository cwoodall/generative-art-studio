## Neuron Website Setup

Use the docker workflow for ease: https://neuron.zettel.page/docker

Run locally:

```
docker run --rm -t -i -p 8080:8080 -v $(pwd):/notes sridca/neuron neuron gen -ws 0.0.0.0:8080
```

### Gotchas

Need to use full paths to the static assets and traditional markdown image links `![]()` instead of obsidian image links `![[]]`

### Plugins and other references

https://neuron.zettel.page/plugins
https://neuron.zettel.page/configuration
