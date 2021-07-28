## M01_SimpleMountains

Code: [M01_SimpleMountains.kt](https://github.com/cwoodall/generative-art-studio/blob/main/kotlin/src/main/kotlin/sketch/M01_SimpleMountains.kt)

- [M01_SimpleMountains](#m01_simplemountains)
  - [Overview](#overview)
  - [Algorithm](#algorithm)
    - [Notes](#notes)
  - [Progression](#progression)
    - [Getting the basic Peak and Valley attractors setup](#getting-the-basic-peak-and-valley-attractors-setup)
    - [Tracing Curves](#tracing-curves)
    - [Drawing Contours and Filling Shapes](#drawing-contours-and-filling-shapes)
    - [Rectangles and Adding Outline](#rectangles-and-adding-outline)
    - [Video Flybys](#video-flybys)
    - [3D Mountain World](#3d-mountain-world)
    - [Getting a nice repeatable view](#getting-a-nice-repeatable-view)
  - [Future Work](#future-work)
  - [Things Learnt](#things-learnt)
### Overview

M01_SimpleMountains started off as an idea to create generative mountain landscapes and evolved into a bit of an experiment in 3d rendering, and animation. This project was done in [[OpenRNDR]] and [[Kotlin]] and I feel like I am starting to see some of the limitations of OpenRNDR in terms not of it's capabilities, but instead in terms of it's documentation and community. 

### Algorithm

This algorithm relieas on the concept of a bunch of "attractors", which in this case actually ended up just being sets of some curve centered around a point with some magnitude and clipped between 0 to the `max_magnitude`. This allows for the concepts of peak attractors and valley attractors. Peaks can ONLY contribute upward movement and valleys can only contribute downward movement. The basic shape of these curves is determined by the following equation:

```
f(x) = clip(1.0 - K*(abs(center - x)/width)^N, 0, 1) * MAGNITUDE
```
 
 This allows for us to feed random numbers into `N` to change the shape of the curve, and K to change the gain. All numbers are scaled here from 0 to 1 internally and then scaled back at the end.
 
 So the algorithm looks like:
 
* drop `P` peaks and `V` valleys and assign each a random SHAPE (`N`) and gain (`K`)
* Sum all of the resulting curves and rescale as needed
* Choose a random mean value
* Create a contour and shape of this value.

As the program started the contours were plotted as particles going across the screen effected by the first derivative of the summed mountain curve. As the program went out I formed an entire shape and then started adding more of them. First I would translate them down the page as a function of their index to make it look like there was a fly by like effect. Eventually I added 3d volumes and 2 methods of drawing:

1. A mesh of the shape
2. A mesh of a bunch of rectangles drawing equal sections of the shape

This ended up being a fun study in 3d rendering, with one major learning: combine as many things into one mesh as possible to save memory.

#### Notes

These are some early notes

![](static/sketches/M01_SimpleMountains/IMG_0934.jpg)

![](static/sketches/M01_SimpleMountains/IMG_0935.jpg)

### Progression
#### Getting the basic Peak and Valley attractors setup
| Raw Peaks and Valleys                                   | Shapes                                                  |    
| ------------------------------------------------------- | ------------------------------------------------------- | 
| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-25-22.54.19.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-25-23.07.22.png) |    

#### Tracing Curves

![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-08.10.46.gif)

| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-25-23.56.42.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-25-23.56.45.png)   |
| ------------------------------------------------------- | --------------------------------------------------------- |
| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-00.10.32.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-00.12.21.png)   |
| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-00.14.18.png) | ![[static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-00.16.13 1.png]] |

![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-08.15.37.gif)

#### Drawing Contours and Filling Shapes

![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-18.46.41.gif)

| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-08.52.02.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-18.37.18.png) |
| ------------------------------------------------------- | ------------------------------------------------------- |
| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-18.57.07.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-18.57.31.png) |

![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-19.00.45.gif)

#### Rectangles and Adding Outline

![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-21.17.17.gif)

![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-22.18.55.gif)
![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-22.41.57.gif)

| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-22.44.33.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-22.53.19.png) |
| ------------------------------------------------------- | ------------------------------------------------------- |
| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-22.53.05.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-22.45.35.png) |

![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-21.33.21.gif)

#### Video Flybys

<embed src="static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-22.52.31.mp4" autostart="false" height="300" width="100%" /></embed>

<embed src="static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-22.52.39.mp4" autostart="false" height="300" width="100%" /></embed>

#### 3D Mountain World

<embed src="static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-22.14.58.mp4"  autostart="false" height="300" width="100%" /></embed>
<embed src="static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-22.13.11.mp4"  autostart="false" height="300" width="100%" /></embed>

| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-21.56.49.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-22.01.47.png) |
| ------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-22.13.30.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-22.14.07.png) |
| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-22.15.51.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-22.15.08.png) |

#### Getting a nice repeatable view

| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-23.29.35.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-23.29.42.png) |
| ------------------------------------------------------- | ------------------------------------------------------- |
| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-23.35.37.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-23.42.18.png) |
| ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-23.43.09.png) | ![](static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-23.43.10.png) |

<embed src="static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-23.42.33.mp4"  autostart="false" height="300" width="100%" /></embed>

<embed src="static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-27-23.43.04.mp4"  autostart="false" height="300" width="100%" /></embed>


### Future Work

- I need to know more about [[Color Theory]] and have better [[Probabilistic Color Palettes]]
- Do this in 3d! This could be really neat, the same concept should scale
- Experiment with adding more mountain shapes and smoothing them with a low pass filter.
- Figure out how to prevent this program from crashing... There is a memory leak somewhere
- [[Generative Terrain Creation]]

### Things Learnt

- [[OpenRNDR#Live programming]]
- [[OpenRNDR#ScreenRecorder Video]]
- Played with subcontours, and other such things.
- Created code for sampling a contour at equal distance spacings and turning it into rectangles (#refactor)
- Created code for creating some reasonable mountains actually
- 3d cameras https://openrndr.discourse.group/t/how-to-use-orbitalcamera-and-orbitalcontrols-in-only-some-parts-of-your-program/53
- Meshes and drawing in 3d

Woah, this was a learning experience for sure... I spent way too much time on this one getting it to something I was happy with, but it got there.