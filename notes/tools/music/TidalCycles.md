## TidalCycles

https://tidalcycles.org/

From the website:

> Tidal Cycles (or 'Tidal') for short is free/open source software written in Haskell. Tidal is using SuperCollider, another open-source software, for synthesis and I/O.
> 
> Tidal Cycles allows you to make patterns with code. It includes language for describing flexible (e.g. polyphonic, polyrhythmic, generative) sequences of sounds, notes, parameters, and all kind of information.

Uses [[Haskell]] underneath and [[SuperCollider]]

### Examples

Don Gorelick's set on youtube inspired me to look into this more:

<iframe width="560" height="315" src="https://www.youtube.com/embed/qdgnRct0_nw?start=29295" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

### Initializing the development environment

#### Startup

```
sclang startup.scd
```

Then open qjackctl to hookup to the actual speaker output (why is this so?)

#### Shutdown

To revert audio reboot pulseaudio after shutting everything down `pulseaudio --kill`

#### Using 

Open vscode (`code`)

`Shift-ENTER` to command  the current line

`ctrl-enter` to execute the block

`Ctrl-Alt-H` to hush

### Using

```
# set to 130 bpm
setcps (130/60/4)

# setup some panels
d1 $ s "[bd:1 ~] * 2"
d2 $ s "[~ hh]*2"

# silence d3
d3 $ silence

# play once
once $ s "cp cp cp"
```
```
# Pattern

hush

d1 $ s "drum" |+| n "2 3" |+| n "4 5 6"

# https://tidalcycles.org/docs/reference/cycles

hush
# https://tidalcycles.org/docs/reference/mini_notation

d1 $ s "[cp | bd(3,8) | hh:2] cp cp"
d2 $ note "[[c8*3 | c7],[c e g c6*3], [e e b [a g]]]" # s "supermandolin"

d3 $ (n "[c'maj ~ e'min g'dom7 a'min ~ g'maj]" # s "superpiano") # gain "0.8 0 0.9 0.7 1 1"

d3 $ silence

hush
```

### Tutorials

https://tidalcycles.org/docs/patternlib/tutorials/workshop/#effects
[Start Tidal | Tidal Cycles](https://tidalcycles.org/docs/getting-started/tidal_start)
