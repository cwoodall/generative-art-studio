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

```# %%
from sovol_xy import SovolSO1, PenState, Rotation
import random

random.seed(123)

with SovolSO1("/dev/ttyUSB1", timeout=0.0) as plotter:
    center_of_image = (50,200.0)

    plotter.autoHome()
    plotter.setTravelSpeed()
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)
    plotter.pause(50)
    plotter.pause(50)
    plotter.moveTo((50+40,200.0+40))
    plotter.setSpeed(1000)
    plotter.pause(50)
    plotter.setPen(PenState.DOWN)
    plotter.pause(50)
    plotter.moveTo((50-40,200.0+40))
    plotter.moveTo((50-40,200.0-40))
    plotter.moveTo((50+40,200.0-40))
    plotter.moveTo((50+40,200.0+40))
    plotter.pause(50)
    plotter.setTravelSpeed()
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)
    plotter.moveTo((50, 200.0))
    plotter.setSpeed(1000)
    plotter.pause(50)
    plotter.setPen(PenState.DOWN)

    for i in range(10):
        center = (random.randrange(-20,20), random.randrange(-20,20))
        if center[0] < 0:
            rot = Rotation.COUNTER_CLOCKWISE
        else:
            rot = Rotation.CLOCKWISE

        plotter.arcTo(center=center, rot=rot)
 
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