## C02_ColorStudy_2dcity

Draw a background of a bunch of rhombuses which are organized to create a 3d optical illusion of a bunch of cubes stacked next to eachother, sort of like a big city

- [[#Algorithm|Algorithm]]
- [[#Progression|Progression]]
	- [[#Not quite right, no detection of collisions with the background|Not quite right, no detection of collisions with the background]]
	- [[#With Outlines and Fills|With Outlines and Fills]]
	- [[#Only Outlines|Only Outlines]]
	- [[#No Outlines|No Outlines]]
- [[#Future work|Future work]]

### Algorithm

Almost the same as [[sketches/C01_ColorStudy_AddingIndex]] due to the increased number of items it became infeasible to calculate all of the shape permutations, and instead I just calculate collisions between the foreground moving items, and the intersections of those intersections (and the foreground items) with the background.

There is a lot that can be done to improve the efficiency.

### Progression

#### Not quite right, no detection of collisions with the background

<embed src="static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.44.26.mp4" autostart="false" height="300" width="100%"></embed>


#### With Outlines and Fills

<embed src="static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.49.47.mp4" autostart="false" height="300" width="100%"></embed>


![static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.49.51.png](static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.49.51.png)
![static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.50.00.png](static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.50.00.png)
![static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.50.49.png](static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.50.49.png)

#### Only Outlines
![static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.52.08.png](static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.52.08.png)

![static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.52.10.png](static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.52.10.png)

![static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.52.24.png](static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.52.24.png)
![[static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.52.06.mp4]]

#### No Outlines

<embed src="static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.52.38.mp4" autostart="false" height="300" width="100%"></embed>
 
![static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.53.18.png](static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.53.18.png)
![static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.53.30.png](static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.53.30.png)
![static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.53.56.png](static/sketches/C02_ColorStudy_2dcity/sketch.C02_ColorStudy_2dcity-2021-07-29-23.53.56.png)

### Future work

- Better way to calculate the intersections... There must be a smarter way of detecting the collisions and reacting to them.