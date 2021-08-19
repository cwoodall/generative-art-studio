# %%
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