---
title: Chris Woodall's Generative Art Studio
feed:
  count: 5
---

- [The Studio](#the-studio)
- [Tools](#tools)
- [Techniques](#techniques)
- [Inspiration](#inspiration)
- [Pieces and Series](#pieces-and-series)
	- [Random](#random)
	- [Flow Fields](#flow-fields)
- [Next actions](#next-actions)
	- [Complet**ed actions](#completed-actions)
	- [Idea holding area](#idea-holding-area)


Generative Art (and [[Creative Coding]]) is a broad term for any computer generated form of art, this could be audio, random, algorithmic, or even mixed media between an algorithm and human input. There are many techniques here and tools. One of the early ones was [[Processing]], which also influenced [[Arduino]] (on the embedded side). I was introduced to the concept through an interview with [[Tyler Hobbs]] (about his [[NFTs]] [[Fidenza]]) which uses a [[Flow Fields]]

### The Studio

[[Studio Setup]]

### Tools

As an overview there are a ton of programming tools, but the main ones I am interested in are:

- [[Nannou]] which uses [[Rust]]
- [[Quil]]
- [[Processing]]
- [[OpenRNDR]]

#### Music

- [[TidalCycles]]
- [[Overtone]]

### Techniques

- [[Flow Fields]]
- [[Probabilistic Color Palettes]]
- [[Cellular Automaton]] 
	- [[Agents or Actors]] and their interactions

- [[Pointalism]]
- [[Fractal Patterns]]
- [[Randomization]]
- [[Areas of Negative Space]]
- [[Composition Anchor points]]
- [[Bezier Curves]]


### Inspiration

[Inspiration for Generative Art](https://raindrop.io/cjwoodall/generative-art-and-creative-coding-18915276)

This video on GitHub Sattelite is also very nice: https://www.youtube.com/watch?v=qdgnRct0_nw&t=12428s

also a reasonable amount of video and music based generative art I would love to get into. Specifically the stuff by Dan Gorelick https://youtu.be/qdgnRct0_nw?t=29068
### Pieces and Series

#### Random

- [[R00_RandomCircles]]
- [[R01_CirclesAndTriangles]]
- [[R02_BranchingArcs]]

#### Flow Fields

- [[F00_FlowFieldsExperiment]]
- [[F01_PerlinNoise]]


### Next actions

- [ ] Attribute management + how to make attributes which have joint probabilities
- [ ] Probabilistic Palette which controls the distribution of a color in an image
- [ ] Composition improvements (Dense spots around which starting points tend to congregate?)
- [ ] Screenshots / Saves should get the name of the piece + seed + date + options (or sizes)

#### Completed actions
- [x] Play with different underlying flow field representations, possibly look into [[Perlin Noise]]
- [x] Make a more generic representation of a flow field
- [x] Build a easier way to generate through using seeds and make a set of images (reproducible from seed once algorithm settled)
- [x] Play with [[Flow Fields]]
- [x] auto-screenshots (see [[R01_CirclesAndTriangles]])
- [x] Make it easier to generate from seeds so art is reproducible.(see [[R01_CirclesAndTriangles]])
- [x] Command-line arguments see [[OpenRNDR#Command Line Arguments]]

#### Idea holding area

- Mountains with various flow fields in them
- Line/box based flow fields similar to [[Fidenza]] with collision detection