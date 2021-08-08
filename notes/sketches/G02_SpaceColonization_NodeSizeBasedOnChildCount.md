## G02_SpaceColonization_NodeSizeBasedOnChildCount

Based on [[G01_SpaceColonization]] with a few efficiency tweaks to the code and the algorithm tweaked slightly to allow for the **seed nodes** to track their **children** and **descendants**. In this case **children** refer to the immediate child nodes and **descendants** is the total number of nodes that are in that branch. This gives an interesting effect that looks like a root system of a tree (or even just a tree)

### Algorithm

The [[Space Colonization Algorithms]] covered in [[G01_SpaceColonization]] is used, but each node tracks it's **children**. With each iteration the size of each node in the network is determined by the following equations: `maxRadius * growthRate * NUM_DESCENDANTS / NUM_CHILDREN`, the result of that is then clamped between `minRadius` and `maxRadius`. This means that the node can only grow so big (this is to prevent it from dominating the screen.

I also added the ability to draw the shape outlines that are being used as the source of the attractors

### Progression

<embed src="static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.04.42.mp4" autostart="false" height="300" width="100%"></embed>

| ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.04.35.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.04.35.png) | ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.G01_SpaceColonization-2021-08-08-11.31.39.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.G01_SpaceColonization-2021-08-08-11.31.39.png) | ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.29.08.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.29.08.png) |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------- |

<embed src="static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.28.21.mp4" autostart="false" height="300" width="100%"></embed>


| ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.15.35.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.15.35.png) | ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.26.32.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.26.32.png) | ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.26.43.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.26.43.png) |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------- |

#### With The Attractor Shapes On

<embed src="static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.25.53.mp4" autostart="false" height="300" width="100%"></embed>

#### Smaller Max Radius and slower growth rate


| ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.49.12 1.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.49.12%201.png) | ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.49.25 1.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.49.25%201.png) |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.49.36 1.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.49.36%201.png) | ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.49.44.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.49.44.png)       | 

<embed src="static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.49.09.mp4" autostart="false" height="300" width="100%"></embed>


| ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.50.37.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.50.37.png) | ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.51.45.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.51.45.png) |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.51.49.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.51.49.png) | ![static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.52.01.png](static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.52.01.png)                                                                                                                                                   |

<embed src="static/sketches/G02_SpaceColonization_NodeSizeBasedOnChildCount/sketch.g02.G02_SpaceColonization_NodeSizeBasedOnChildCount-2021-08-08-12.51.33.mp4" autostart="false" height="300" width="100%"></embed>


### Future Work

I am mostly happy with this. I also took some major strides forwards in reproducibility from a given seed and number of iterations, before the colors were still completely random and uncoupled from the structure, now they are linked together

- [ ] Create a library for this algorithm
- [ ] Draw paths from the **seed nodes** outwards, keeping track of the children was step one here. This is also referred to in [[G01_SpaceColonization#Future Work]]