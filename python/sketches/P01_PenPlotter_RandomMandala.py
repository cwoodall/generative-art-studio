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