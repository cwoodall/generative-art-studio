## G01_SpaceColonization

Using [[OpenRNDR]] to make a [[Space Colonization Algorithms]] to make some leaf like, tree like and maybe even different types of natural shapes and formation. Using [Jason Webb's](https://medium.com/@jason.webb/space-colonization-algorithm-in-javascript-6f683b743dc5) and the following papers ([1](http://algorithmicbotany.org/papers/venation.sig2005.pdf), [2](http://algorithmicbotany.org/papers/colonization.egwnp2007.large.pdf)) as a baseline. This involves having a bunch of nodes, these are the points that have already grown, and attractors (places which the nodes are growing towards). Each attractor then has a field of influence, for each of those nodes we calculate the average direction acting on the node. Draw the next segment, place our new nodes, and then finally kill off the attractors if a node get's to close to it (and repeat).

### The Algorithm

1. Place attractors
2. Associate the attractors with nearby nodes (within some **attraction radius**/**field of influence**). For now only take the **NEAREST** node
3. Iterate through the nodes and grow the network (**segment length**)
	1. If two nodes would collide we do not add the new node (**collision radius**)
4. Kill the attractors (**kill radius**)
5. Prune any lone nodes (nodes which never became associated with an attractor) after a few counts

To do this we build a [[Spatial Indexing|Spatial Index]] using a [[QuadTree]]. Luckily there was one already built in the [[OpenRNDR Extension Library]], however it was not in the version of [[OpenRNDR]] I was building against, so I just copied it in to my `util` directory for use in this algorithm. The [[QuadTree]] gets built at the beginning of every iteration. When doing the searches for the 

Each source node will **inherit it's color** from it's parent node (in this iteration of the algorithm)

For this iteration we will just draw a circle for every node.

### Progression

#### Using the QuadTree and Getting the Initial Algorithm Right 

Random attractors with one **seed node** at the center

| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.05.54 1.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.05.54%201.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.08.58 1.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.08.58%201.png) |
| --------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
                                                                                           
#### Attractors placed along circles

Attractors are placed along circles with a few starting nodes. Starting with 1 circle then adding more

| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.17.20.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.17.20.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.19.19.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.19.19.png) |
| --------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.19.06.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.19.06.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.19.36.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.19.36.png) |

<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.16.58.mp4" autostart="false" height="300" width="100%"></embed>

<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.19.00.mp4" autostart="false" height="300" width="100%"></embed>

<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.25.15.mp4" autostart="false" height="300" width="100%"></embed>

#### Adding Rectangles, lines and randomizing the center locations
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.26.19.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.26.19.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.26.29.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.26.29.png) | 
| --------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |

<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.26.00.mp4" autostart="false" height="300" width="100%"></embed>

<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.47.19.mp4" autostart="false" height="300" width="100%"></embed>


#### Associating each *seed node* with a color 

| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.31.04.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.31.04.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.31.15.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.31.15.png) |
| --------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.32.09.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.32.09.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.32.49.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.32.49.png) |
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.33.53.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.33.53.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.34.08.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.34.08.png) |
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.34.40.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.34.40.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.35.02.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.35.02.png) | 

<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.54.30.mp4" autostart="false" height="300" width="100%"></embed>
	
<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-08.34.00.mp4" autostart="false" height="300" width="100%"></embed>

<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-08.41.14.mp4" autostart="false" height="300" width="100%"></embed>

#### More color palettes
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.48.59.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.48.59.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.54.41.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-00.54.41.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-08.37.11.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-08.37.11.png) |
| --------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-08.37.45.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-08.37.45.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-08.43.50.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-08.43.50.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.05.19.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.05.19.png)                                                                     | ![[static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.07.11.png]]                                                                     |                                                                                                                                               |
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.09.41.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.09.41.png)                                                                     | ![[static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.14.11.png]]                                                                     | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.25.19.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.25.19.png)

<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.04.29.mp4" autostart="false" height="300" width="100%"></embed>


<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.25.53.mp4" autostart="false" height="300" width="100%"></embed>


#### Some different drawing styles

Bringing the segment lengths closer together

| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.28.37.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.28.37.png) |  ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.28.55.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.28.55.png) |
| --- | --- |
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.31.29.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.31.29.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.34.12.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.34.12.png)
| ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.46.51.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.46.51.png) | ![static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.27.01 1.png](static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.27.01%201.png)

<embed src="static/sketches/G01_SpaceColonization/sketch.G01_SpaceColonization-2021-08-07-09.44.55.mp4" autostart="false" height="300" width="100%"></embed>

### Glossary

**segment length**
**attraction radius**
**collision radius**
**kill radius**
**attractor**
**node**
**seed node**

### Future Work

- Refactor so that we used a doubly linked list so we can determine the number of children or who the parent is.
- Draw paths not dots
- Opacity and thickness effects based on node size
- gradient based on attractor color and node color.
- Add boundaries, and obstacles
- Batch the circle drawing by color