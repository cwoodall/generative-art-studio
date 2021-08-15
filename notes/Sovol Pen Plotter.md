## Sovol Pen Plotter

[[Using OpenRNDR with Pen Plotter]]

### Communications

Uses [[GCode]]

Connects as a serial port (`/dev/ttyUSB0` for example) at `115200`

See https://github.com/Springwald/GCodePlotter
https://github.com/Springwald/GCodePlotter/blob/main/code/Plotter/SovolS01Hardware.cs

make sure you add your user to dialout: 

```
sudo usermod -a -G dialout ${USER}
sudo usermod -a -G dialout ${USER}
```

If dialout does not exist make it `newgrp dialout`

Need to home first, otherwise wherever the machine stats becomes 0
Connecting and disconnecting reboot.

https://www.klipper3d.org/G-Codes.html

Supports `G2` and `G3` for clockwise and counter clockwise arcs

Uses the "Marlin" firmware. Here is the G-Code reference for the [Marlin Firmware](https://marlinfw.org/meta/gcode/)


Startup message

```
Marlin 1.1.6

 Last Updated: 2019-12-16 | Author: SO-1
Compiled: Mar 16 2020
 Free Memory: 10510  PlannerBufferBytes: 1232
EEPROM version mismatch (EEPROM=? Marlin=V41)
Hardcoded Default Settings Loaded
  G21    ; Units in mm
  M149 C ; Units in Celsius

Filament settings: Disabled
  M200 D1.75
  M200 D0
Steps per unit:
  M92 X80.00 Y80.00 Z400.00 E140.00
Maximum feedrates (units/s):
  M203 X300.00 Y300.00 Z5.00 E25.00
Maximum Acceleration (units/s2):
  M201 X1000 Y1000 Z100 E1000
Acceleration (units/s2): P<print_accel> R<retract_accel> T<travel_accel>
  M204 P1000.00 R1000.00 T1000.00
Advanced: S<min_feedrate> T<min_travel_feedrate> B<min_segment_time_ms> X<max_xy_jerk> Z<max_z_jerk> E<max_e_jerk>
  M205 S0.00 T0.00 B20000 X10.00 Y10.00 Z0.40 E5.00
Home offset:
  M206 X0.00 Y0.00 Z0.00
Material heatup parameters:
  M145 S0 H185 B45 F0
  M145 S1 H240 B70 F0
PID settings:
  M301 P29.76 I2.93 D75.58
Z-Probe Offset (mm):
  M851 Z0.00
TF card ok
Init power off infomation.
size: 
585
init valid: 
```


```mermaid
graph s
```
### Axidraw

https://wiki.evilmadscientist.com/AxiDraw