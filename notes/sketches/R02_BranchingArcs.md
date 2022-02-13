# R02_BranchingArcs

Using [[OpenRNDR]]

### Working Log

starting with simple random circles then drawing arcs of various lengths with probabilistic times where the arc ends

- Added arcs
- Need to make trees
- Need to make it so the circles radius can go in either direction
- Added basic collision detection, but it needs improvement
- Arcs go on both ways now
- Generally rather satisfying to watch, but the end results are questionable
- Take video for this one.

### Algorithm

- Create a random circle of some radius within the picture frame (preserving some margin)
- Choose a random point on that circle
- Start to step the arc forward, with each step
	- Roll for branching into a new connected circle
		- If branching into a new circle, choose a random radius less than the present radius. Then choose if the radius will be inside or outside the previous circle and if it will step clockwise or counter clockwise.

	- Check for collisions or end of tree conditions (roll for it, or cirlce is smaller than some radius)
		- Start a new tree


### Progression

#### Started drawing random circles and curves
| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-22-23.16.08.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-22-23.16.09.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-22-23.16.21.png) |
| --- | --- | --- |
| | | |

#### No limits, collision detection, etc

| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-22-23.42.38.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-22-23.42.53.png) |
| --- | --- |
| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-00.02.34.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-00.02.36.png) |
| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-00.02.40.png) | |

#### Run for a while

![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-00.03.26.png)

#### Collision detection

| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-07.52.29.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-07.54.01.png) |
| --- | --- |
| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-07.56.51.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-07.58.52.png) |
| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-07.59.10.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-07.59.57.png) |
| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-08.01.03.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-08.01.24.png) |
| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-08.05.31.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-08.07.20.png) |

#### Sparser

| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-19.21.48.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-19.24.22.png) |
| --- | --- |
| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-19.26.48.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-19.27.26.png) |
| ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-19.28.36.png) | ![](static/sketches/R02_BranchingArcs/sketch.R02_BranchingArcs-2021-07-23-19.29.17.png) |


#### Video