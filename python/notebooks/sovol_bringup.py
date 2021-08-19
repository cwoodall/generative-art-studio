# To add a new cell, type '# %%'
# To add a new markdown cell, type '# %% [markdown]'
# %%
#

# %% [markdown]
# # Initial Bringup of the Sovol-SO1
# 
# Much of these are taken from: https://github.com/Springwald/GCodePlotter/blob/main/code/Plotter/SovolS01Hardware.cs
# 
# It was learnt that the Sovol-SO1 does not require a \r, only \n is used
# 
# Shows up as a USB to serial device, with a 115200 baud rate.

# %%
# Requires pyserial library to be installed
import serial
serial_port = "/dev/ttyUSB0" # Set this to whatever tty serial dev
baudrate = 115200
serial = serial.Serial(serial_port, baudrate)


# %%
serial.close()


# %%
# Clear the buffer
serial.write(b"\n\r")
time.sleep(1.0)
res = serial.read_all()
print(res)


# %%
# Get some basic settings set
serial.write(b"G90\n\r") # Set to absolute mode
serial.write(b"G21\n\r") # Set to mm


# %%
# Pen Down
serial.write(b"M280P0S0\n\r")


# %%
# Pen Up (HIGH)

serial.write(b"M280P0S90\n\r")


# %%
# Pen Up (Normal)

serial.write(b"M280P0S30\n\r")


# %%
# Pause 
serial.write(b"M280P0S0\n\r") # pen down
serial.write(b"G4 P2000\n\r") # P50 is the pause length in ms
serial.write(b"M280P0S30\n\r") # pen up


# %%
# Auto home
serial.write(b"G28\n\r")


# %%
# Go to position
x = 100
y = 120.0

max_y = 300 #mm
max_x = 300 #mm
sendstr = f"G1 X{x:0.3f} Y{y:0.3f}\n\r".encode('utf-8')
print(sendstr)
serial.write(sendstr)


# %%
# Travel speed
serial.write(b"G1 F10000\n\r")


# %%
#Paint Speed
# Travel speed
serial.write(b"G1 F3000\n\r")


# %%
import time
with SovolSO1("/dev/ttyUSB0") as plotter:
    plotter.autoHome()
    plotter.setTravelSpeed()
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)
    plotter.moveTo((75.0,75.0))
    plotter.setDrawingSpeed()
    plotter.setPen(PenState.DOWN)
    plotter.pause(50)
    plotter.moveTo((100.0,100.0))
    plotter.moveTo((50.0,100.0))
    plotter.moveTo((75.0,75.0))
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)
    plotter.moveTo((50.0,100.0))
    plotter.setPen(PenState.DOWN)
    plotter.pause(50)
    plotter.moveTo((50.0,50.0))
    plotter.moveTo((75.0,75.0))
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)
    plotter.moveTo((50.0,50.0))
    plotter.setPen(PenState.DOWN)
    plotter.pause(50)
    plotter.moveTo((100.0,50.0))
    plotter.moveTo((75.0,75.0))
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)
    plotter.moveTo((100.0,50.0))
    plotter.setPen(PenState.DOWN)
    plotter.pause(50)
    plotter.moveTo((100.0,100.0))
    plotter.arcTo((50.0,100.0), (-25.0,-25.0), Rotation.COUNTER_CLOCKWISE)
    plotter.arcTo((50.0,50.0), (25.0,-25.0), Rotation.COUNTER_CLOCKWISE)
    plotter.arcTo((100.0,50.0), (25.0,25.0), Rotation.COUNTER_CLOCKWISE)
    plotter.arcTo((100.0,100.0), (-25.0,25.0), Rotation.COUNTER_CLOCKWISE)
    plotter.setPen(PenState.HIGH_UP)
    plotter.pause(50)
    plotter.setTravelSpeed()
    plotter.moveTo((0.0,0.0))

    


