## C01_ColorStudy_AddingIndex

Moving regions of code which will do some level of mod math on overlapping sections.

### Algorithm

- Spawn a few shapes and assign them color indexes and velocities
- With each iteration:
	- Find all of the overlapping intersections and sum the color indexes in those regions
	- Take the resulting color index and wrap it around by the number of colors in the palette ( `index % NUM_COLORS`)
	- Calculate where all of the shapes will be in the next step
	- Draw it!

#### The Wrapped Color Palette Math

Consider the following color palette with the following assigned indexes:

<center>
![static/sketches/C01_ColorStudy_AddingIndex/color_palette_math.drawio.png](static/sketches/C01_ColorStudy_AddingIndex/color_palette_math.drawio.png)
</center>
When 2 colors are overlapped we `ADD` together the colliding colors and `mod` by the number of colors in the palette. This essentially creates a wrapped palette. For example:

- `(0 + 1) % 5 = 1`
- `(4 + 1) % 5 = 0`
- `(3 + 4) % 5 = 2`
- `(3 + 4 + 1) % 5 = 3`

This creates a sort of interesting result, since the relationships are not determined by any sort of color theory, but instead is based by the relationship of the indexes. it is also interesting, since some combinations may result in no change. (`0 + 0 = 0`). Here is am example as a simple overlap:

<center>
![static/sketches/C01_ColorStudy_AddingIndex/color_palette_math_example.drawio.png](static/sketches/C01_ColorStudy_AddingIndex/color_palette_math_example.drawio.png)
</center>

### Progression

#### Trying to get the overlaps right

![static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-28-22.14.56.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-28-22.14.56.png)

![static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-28-22.16.29.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-28-22.16.29.png)

![static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-28-22.17.14.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-28-22.17.14.png)

#### All of the intersections calculated

I had to calculate all unique combinations of all of the shapes then construct the intersections and sort so that the intersection with the most shapes was on bottom (drawn last) so that it is not covered up by the other shapes.

![static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-28-23.29.32.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-28-23.29.32.png)


#### Adding some rotations

![static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.03.03.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.03.03.png)

![static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.03.04.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.03.04.png)

#### Some randomness

![static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.04.14.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.04.14.png)

![static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.04.20.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.04.20.png)

![static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.08.09.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.R03_ColorLogic-2021-07-29-00.08.09.png)

One thing I noticed while playing with the black and white is any index of 0 will just nullify itself. So for example: `(0 + 0) % 1 = 0`, `(1 + 1) % 2 = 0`. I decided this logic is ok, but there is room for other logics here, or even just use of opacity, [[Shaders]] or some [[N-value logic]] or gradients. 

#### Adding More Shapes

![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.24.25.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.24.25.png)
![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.24.29.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.24.29.png)
![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.24.53.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.24.53.png)
![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.25.19.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.25.19.png)

#### Outlines

![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.43.26.gif](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.43.26.gif)

![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.43.48.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.43.48.png)

![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.43.51.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.43.51.png)

![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.44.06.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.44.06.png)

<embed src="static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_CirclesOverlap-2021-07-29-07.58.53.mp4" autostart="false" height="300" width="100%"></embed>

#### Outline and Drawing Modes

![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_ColorStudy_AddingIndex-2021-07-29-11.20.20.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_ColorStudy_AddingIndex-2021-07-29-11.20.20.png)

![static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_ColorStudy_AddingIndex-2021-07-29-11.20.47.png](static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_ColorStudy_AddingIndex-2021-07-29-11.20.47.png)

<embed src="static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_ColorStudy_AddingIndex-2021-07-29-11.19.47.mp4" autostart="false" height="300" width="100%"></embed>

<embed src="static/sketches/C01_ColorStudy_AddingIndex/sketch.C01_ColorStudy_AddingIndex-2021-07-29-11.20.40.mp4" autostart="false" height="300" width="100%"></embed>

### Future work

- Shaders to speed up th drawing
- play with opacity
- incorporate the background color into the shapes
- Random shapes instead of predestined shapes
- Gradients of mono-hue and saturation with variable value/brightness!? or even fixed s and v and mono hue Or hey we can just navigate through the color space and find the median distance between points.	
- Sketch outlines coloring instead of the fills

Using [[OpenRNDR]]