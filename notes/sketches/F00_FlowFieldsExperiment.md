## F00_FlowFieldsExperiment
Tool: [[OpenRNDR]]
Concepts: [[Flow Fields]]

### Overview

Some basic experiments with an initial [[Flow Fields]] which only produces angles (no magnitudes). The basic algorithm uses a few random scalers and offsets to feed the field with the following equation:

```kotlin
var angle =  
  360 * (scaler_a * (x_scaled + x_offst) * (y_scaled + y_offst) + (y_scaled) * scaler_b)
```

This produces a field with quite a bit of variety.

This uses the same palette as I used in [[R00_RandomCircles]] whcih was taken from [ericyd on Github](https://github.com/ericyd/generative-art/blob/main/openrndr/src/main/kotlin/sketch/S30_Dunes.kt). I also added some basic extensions for [[Using OpenRNDR to take Screenshots]] so that I can press the `SPACE` bar and take a screenshot (then progress to the next image) or press the mouse button to continue using [[OpenRNDR in Presentation Mode]]


While making this I also added a few helper functions for generating random vectors, rounding to the nearest N where N is some Double, and other useful utilities.

### Progression

#### Drawing the flow fields

Initially I played around with generating and drawing the flow fields.

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-00.55.39.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-00.56.10.png)

### Generate a few Single Lines

I decided to create circles at spacings to trace out the lines in sort of [[Pointalism]] inspired style. These were randomly distributed starting points on the flow field (allowed to start be outside of the canvas)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-01.43.43.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-01.53.44.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-02.09.17.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-02.21.06.png)

And I started playing with the underlying flow field and adding parameters to change its orientation, location, and also start to modulate the **density** and **thickness** of the lines and relative spacing to try to see what range i could get

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-03.03.33.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-03.03.37.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-03.03.43.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-03.03.46.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-03.03.53.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-03.04.06.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.17.46.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.28.39.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.28.47.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.28.54.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.31.09.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.31.16.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.31.30.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.32.45.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.32.47.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.34.18.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.34.20.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.34.23.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.34.46.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.40.44.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.40.46.png)

#### Attributes

Started generating boolean values that would modulate things like if we were using sharp edges or smooth curves (a technique of binning the flow field points into the spacing, essentially snapping them in after they have traveled their spacing distance), and also density and chunkyness. 

These attributes need some work and some more formality. [[Joint Probabilities]] would be pretty useful to control the relationships of the attributes to eachother.

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.58.56.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.58.58.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-10.59.03.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.00.49.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.08.33.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.09.57.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.10.01.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.12.43.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.12.47.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.23.38.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.29.43.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.54.38.png)

![](static/sketches/F00_FlowFieldsExperiment/sketch.F00_FlowFieldsExperiment-2021-07-10-11.54.39.png)

### Thoughts

- I sort of like the simple line drawings better (single circles), they have more flow, the others tend to be chaotic
- I like the little bits of randomness in the drawing if done right. A gaussian noise might be best here
- Composition is lacking
- I like the sharp edges

### Future Work

- Tons to be done! fertile ground
- Different fields
- Attractors
- [[Perlin Noise]]
- Improve attributes
- Improve composition
- Try with different shapes and line types
- Finish organizing these notes