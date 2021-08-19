## P01_PenPlotter_RandomMandala

Made using [[Python]] and a [[Sovol Pen Plotter]]. This is drawn directly from [[Python]] using serial communication with the [[Sovol Pen Plotter]]. I did this by communicating with the pen plotter over serial using a library I wrote, [sovol_xy](https://www.github.com/cwoodall/sovol_xy) and released to pypi (installable via pip). The sketch itself was done as a python script (originally an .ipynb).

- Code: [P01_PenPlotter_RandomMandala.py](https://github.com/cwoodall/generative-art-studio/blob/main/python/sketches/P01_PenPlotter_RandomMandala.py)

### Algorithm

- Choose your **origin**. 
- Draw N (N=100) random arcs with random centers, with the chosen **origin** on the perimeter of that circle.
	- This uses the `G2` and `G3` commands to create clockwise and counterclockwise circles, directly using GCode

### Results

![](static/sketches/P01_PenPlotter_RandomMandala/P01_PenPlotter_RandomMandala_Output1.jpg)
<embed src="static/sketches/P01_PenPlotter_RandomMandala/P01_PenPlotter_RandomMandala.mp4" autostart="false" height="300" width="100%"></embed>


I did not find this pen plotter to be particularly accurate

### Code

The following code is a little sloppy, but gives a good example of how to use the `sovol_xy` library:

```python
# Example #1 Lines and Arcs
# Draw a frame and then some random circles inside of it.
# Set the seed to different values to get different arcs
from sovol_xy import SovolSO1, PenState, Rotation
import random
import numpy as np

random.seed(123)

with SovolSO1("/dev/ttyUSB1", timeout=0.0) as plotter:
    origin = np.array((100, 100.0))
    max_circle_radius = 30
    margin = .25
    box_size = max_circle_radius * (2 + margin)

    plotter.autoHome()
    plotter.setTravelSpeed()
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)

    # Draw a rectangle around the origin
    # Move to the edge of the box
    plotter.moveTo( origin + box_size * np.array((1,1)))

    # Put the pen down for drawing
    plotter.setSpeed(3000)
    plotter.pause(50)
    plotter.setPen(PenState.DOWN)
    plotter.pause(50)

    # Draw a box
    plotter.moveTo(origin + box_size * np.array((-1,1)))
    plotter.moveTo(origin + box_size * np.array((-1,-1)))
    plotter.moveTo(origin + box_size * np.array((1,-1)))
    plotter.moveTo(origin + box_size * np.array((1,1)))
    plotter.pause(50)

    # Move to the origin
    plotter.setTravelSpeed()
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)
    plotter.moveTo(origin)
    plotter.setSpeed(3000)
    plotter.pause(50)
    plotter.setPen(PenState.DOWN)

    for i in range(10):
        # Select random center of  the circle
        center = (
            random.randrange(-max_circle_radius,max_circle_radius), 
            random.randrange(-max_circle_radius,max_circle_radius)
        )

        # If we are in the left half plane rotate counter clockwise
        if center[0] < 0:
            rot = Rotation.COUNTER_CLOCKWISE
        # Otherwise rotate clockwise
        else:
            rot = Rotation.CLOCKWISE

        # Draw the circle
        plotter.arcTo(center=center, rot=rot)
 
    # Return to home
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)   
    plotter.setTravelSpeed()
    plotter.moveTo((0.0,0.0))
```

### Future work

- SVGs.
- More complicated paths.
- Integrate with OpenRNDR
- Actual mathematically interesting drawings rather than just some random circles.