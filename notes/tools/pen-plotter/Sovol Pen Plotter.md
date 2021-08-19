## Sovol Pen Plotter

[[Using OpenRNDR with Pen Plotter]]

### Communications

Uses [[GCode]]

Connects as a serial port (`/dev/ttyUSB0` for example) at `115200`

See https://github.com/Springwald/GCodePlotter
https://github.com/Springwald/GCodePlotter/blob/main/code/Plotter/SovolS01Hardware.cs

#### Setup

make sure you add your user to  the `dialout` group: 

```
sudo usermod -a -G dialout ${USER}
```

If dialout does not exist make it `newgrp dialout`

#### Startup Notes

- Need to issue the auto-home first to zero the machine to the bottom left hand corner of the machine
- Since we are using stepper motors, there is no position feedback, so as you move the robot by hand that will become zero. So you can place zero wherever you want on the page.
- On startup the following message is sent:
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

#### GCode Support

- Supports `G1` for straightline moves
- Supports `G2` and `G3` for clockwise and counter clockwise arcs
- Does not support `G5` for bezier splines

- Uses the "Marlin" firmware. Here is the [[G-Code]] reference for the [Marlin Firmware](https://marlinfw.org/meta/gcode/)

- When sending G-Code commands wait for `ok` to return before continuing to help rate limit the machine

#### Todo

- [x] Disable motors and close so they do not overheat
- [x] See if I can get bezier curves to work (probably not). Was not able to get `G5` to work
- [ ] Package and release as a library for [[Python]]
- [ ] Port to java for use with OpenRNDR
- [ ] Write up post
- [ ] Reproducibility is low
- [ ] Write-up a bring-up blog post, since I think that would be useful.
- [ ] Autoconnect
	- [ ] Shows up as: Bus 001 Device 009: ID 1a86:7523 QinHeng Electronics HL-340 USB-Serial adapter:
	- Bus 001 Device 010: ID 1a86:7523 QinHeng Electronics HL-340 USB-Serial adapter


#### Startup Sequence

- Connect to serial port
- Wait for bootup sequence (if necessary)
- Set the mode to absolute, set the units to mm
- Auto-home
- Wait for home to be done
- Move to starting location
- pen down
- draw
- move to next location, etc.
-

#### Ideas

- Suction cup for a basic pick and place machine
- Add vision?
- Bed leveling?
- Pressure feedback for painting?
- Could it do water colors??? It would be fun to try
- Interactive / collaborative art with a webcam + cv (https://towardsdatascience.com/live-video-sketching-through-webcam-using-computer-vision-30beed29f33e)
- ob

