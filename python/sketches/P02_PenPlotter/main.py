# TODO: Does not seem to handle the rectangular paths correctly
#%%
from xml.dom import minidom
from svg.path import parse_path, Path
import math
from sovol_xy.sovol_xy import Point
import time

def toPoint(line: complex) -> Point:
    return (line.imag, line.real)

svg_dom = minidom.parse("1223-1.svg")

path_strings = [path.getAttribute('d') for path in svg_dom.getElementsByTagName('path')]

lines = [parse_path(p_str)._segments[-1] for p_str in path_strings]
lines.sort(key=lambda x: x.start.real)

# Scale lines from pixels to 
# 1 px = 0.264583333 mm
for line in lines:
    line.start = line.start *  0.15
    line.end = line.end *  0.15

# Group lines together into longer lines
lines_of_lines = []
while len(lines) > 1:
    new_lines = [lines.pop()]
    
    searching = True
    idx = 0
    while searching and idx < len(lines):
        distance = math.dist(toPoint(lines[idx].start), toPoint(new_lines[-1].end))
        if (abs(distance) < 1e-4):
            new_lines.append(lines.pop(idx))
            idx = 0

        distance = math.dist(toPoint(lines[idx].end), toPoint(new_lines[-1].end))
        if (abs(distance) < 1e-4):
            li = lines.pop(idx)
            st = li.start
            li.start = li.end
            li.end = st
            new_lines.append(li)
            idx = 0

        distance = math.dist(toPoint(lines[idx].start), toPoint(new_lines[0].start))
        if (abs(distance) < .001):
            li = lines.pop(idx)
            st = li.start
            li.start = li.end
            li.end = st
            new_lines.insert(0, li)
            idx = 0

        distance = math.dist(toPoint(lines[idx].end), toPoint(new_lines[0].start))
        if (abs(distance) < 1e-4):
            li = lines.pop(idx)
            new_lines.insert(0, li)
            idx = 0


        idx += 1
        if (idx >= len(lines)):
            searching = False
    lines_of_lines.append(new_lines)

def point_distance(a, b):
    return math.dist(toPoint(a), toPoint(b))

def reverse_line(line):
    # Reverse the line
    line.reverse()
    for segment in line:
        temp = segment.end
        segment.start = segment.end
        segment.end = temp

for line in lines_of_lines:
    if math.dist((0.0, 0.0), toPoint(line[0].start)) > math.dist((0.0, 0.0), toPoint(line[-1].end)):
        line = reverse_line(line)

# Sort the lines based on their starting points distance
lines_of_lines = [line for line in lines_of_lines if len(line) > 1]
lines_of_lines.sort(key=lambda line: math.dist((0.0, 0.0), toPoint(line[0].start)))

new_lines_of_lines = []

# new_lines_of_lines.append(lines_of_lines.pop(0))

# # Use a greedy algorithm to order the lines to minimize pen up time
# while len(lines_of_lines) > 0:
#     next_line_idx = 0
#     min_distance = math.inf
#     reverse = False
#     for idx, line in enumerate(lines_of_lines):  
#         distance = point_distance(new_lines_of_lines[-1][-1].end, line[0].start)
#         if distance < min_distance:
#             next_line_idx = idx
#             reverse = False
#             min_distance = distance

#         distance = point_distance(new_lines_of_lines[-1][-1].end, line[-1].end)
#         if distance < min_distance:
#             next_line_idx = idx
#             reverse = True
#             min_distance = distance

#     next_line = lines_of_lines.pop(next_line_idx)
#     if reverse:
#         next_line = reverse_line(next_line)
#     if next_line:
#         new_lines_of_lines.append(next_line)

# lines_of_lines = new_lines_of_lines
#%%
# Attach to the SovolSO1
from sovol_xy import SovolSO1, PenState, Rotation
import random
import numpy as np
import logging
logging.getLogger().setLevel(logging.INFO)
with SovolSO1("/dev/pts/5", timeout=0.0, startup_timeout=0.0) as plotter:
    plotter.autoHome()
    plotter.setTravelSpeed()
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(100)
    plotter.moveTo(toPoint(lines_of_lines[0][0].start))
    for line in lines_of_lines:
        try:
            # Move to the origin
            plotter.setTravelSpeed()
            plotter.setPen(PenState.HIGH_UP)
            plotter.pause(100)
            plotter.moveTo(toPoint(line[0].start))
            # Put the pen down for drawing
            plotter.setSpeed(3000)
            plotter.pause(100)
            plotter.setPen(PenState.DOWN)
            plotter.pause(100)
            plotter.pause(100)
            for pt in line:
                # Draw a box
                plotter.moveTo(toPoint(pt.start))
                plotter.moveTo(toPoint(pt.end))
            plotter.pause(200)
            plotter.setTravelSpeed()
            plotter.setPen(PenState.HIGH_UP)
            plotter.pause(200)
        except Exception as e:
            print(e)
            continue
    plotter.setTravelSpeed()
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(100)
    plotter.moveTo((0,0))
    plotter.disableSteppers()
