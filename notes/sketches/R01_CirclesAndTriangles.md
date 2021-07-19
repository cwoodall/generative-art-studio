## R01_CirclesAndTriangles

Circles And Triangles is part of the Random Series. The goal was to get acquainted with [drawing contours](https://guide.openrndr.org/#/04_Drawing_basics/C05_ComplexShapes?id=placing-points-on-contours) in [[OpenRNDR]].  These pieces have a limited pallete of a background color (black) and a foreground color (white)

### Algorithm

1. Randomly decided on the number of circles, arcs on those circles, and triangles which will be drawn inscribed into those circles.
2. Generate `N` circles with different center points and radii
3. For each iteration of the algorithm:
	1. Until all of the triangles are used up
		1. Choose 2 random points on the circle
		2. Choose a 3rd point at the center of the circle, outside the circle, or inside the circle whose angle within that circle lies between the previous 2 points
		3. Draw a triangle, with some chance of creating an arc between the first 2 points at the radius of the circle.
	2. Until all arcs are used up
		1. Create an arc between two random points on the circle with
			1. A chance at slightly altered bezier curves
			2. A chance at a different radius (Gaussian distribution)
4. Draw it and take a screenshot


### Progression

#### Started out with just flat 1 px lines

| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-21.55.04.png)   | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.35.48.png) |    ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.37.43.png)                                                  |
| ------------------------------------------------------ | ---------------------------------------------------- | ---------------------------------------------------- |
| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-21.53.35.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-21.53.40.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-21.53.41.png) |

#### Starting to play with circle size

| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.40.38.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.40.41.png) |![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.40.50.png)
| -- | --| -- |
| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.42.38.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.42.41.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.43.32.png)

#### Arcs and Weight


| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.40.55.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.54.38.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.54.42.png) |
| -- | -- | -- |
| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.54.47.png) | | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-22.54.50.png) | 

#### Multiple Circles

| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.01.19.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.01.39.png) |
| -- | -- |
| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.01.49.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.02.40.png) |
| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.05.16.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.05.41.png) |
| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.22.30.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.22.31.png) |
| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.22.57.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-18-23.23.27.png) |
| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-19-00.24.29.png) | ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-19-00.28.32.png) |
| ![](static/sketches/R01_CirclesAndTriangles/sketch.R01_BezierCurves-2021-07-19-07.21.15.png) | |

## Future Work

I will probably leave this one here for now, but I am interested in:

- being able to autogenerate from the commandline from various seeds
- Adding more color to the drawings, even if it is just random different 2 done palletes

I will return to this concept for sure