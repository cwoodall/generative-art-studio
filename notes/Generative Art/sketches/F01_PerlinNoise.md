# F01_PerlinNoise

## Log

Started  by looking at 

- https://openrndr.discourse.group/t/openrndr-processing-noise-fields-leaving-trails/215

Had to scale the [[Perlin Noise]] to get it to work correctly (once to get inputs from 0 to 1 then a second time to get outputs closer to 0 to 1).

Initial results (with field angles on in the background)
![[sketch.F01_PerlinNoise-2021-07-13-22.58.56.png]]

![[sketch.F01_PerlinNoise-2021-07-13-22.59.01.png]]

Then starting to produce a few single thickness works

![[sketch.F01_PerlinNoise-2021-07-13-23.15.35.png]]

![[sketch.F01_PerlinNoise-2021-07-13-23.16.21.png]]

![[sketch.F01_PerlinNoise-2021-07-13-23.17.58.png]]

![[sketch.F01_PerlinNoise-2021-07-13-23.20.02.png]]

I made a basic abstraction for a palette and FlowFields so I can sub in different fileds into the same code easier

Trying some with lots of points and a black background:

![[sketch.F01_PerlinNoise-2021-07-13-23.27.45.png]]

![[sketch.F01_PerlinNoise-2021-07-13-23.28.04.png]]

![[sketch.F01_PerlinNoise-2021-07-13-23.28.23.png]]

Here are a few with a ton of lines

|                                                     |                                                     |
| --------------------------------------------------- | --------------------------------------------------- |
| ![[sketch.F01_PerlinNoise-2021-07-13-23.35.46.png]] | ![[sketch.F01_PerlinNoise-2021-07-13-23.36.32.png]] |
| ![[sketch.F01_PerlinNoise-2021-07-13-23.37.27.png]] | ![[sketch.F01_PerlinNoise-2021-07-13-23.38.05.png]] |
|                                                     |                                                     |

Topics: [[Perlin Noise]], [[OpenRNDR]]