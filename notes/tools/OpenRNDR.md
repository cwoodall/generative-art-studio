## OpenRNDR

A [[Kotlin]] based framework for [[Creative Coding]]

### References

https://api.openrndr.org/
https://github.com/openrndr/orx

#### Custom Frame Rates

https://openrndr.discourse.group/t/running-a-program-at-a-specific-frame-rate/144

#### Live programming

https://openrndr.discourse.group/t/improved-live-coding-with-orx-olive/106
Example: https://www.youtube.com/watch?v=qdgnRct0_nw&t=12428s
https://guide.openrndr.org/#/10_OPENRNDR_Extras/C03_Live_coding


### Extensions

#### Video
#### Screenshot

Use the space bar to take a screenshot

```
import org.openrndr.extensions.Screenshots

...
extend(Screenshots())
```

#### NoClear

Don't clear after every frame (allows for more of a canvas like approach)

To use need to add:
```
"orx-no-clear",
```
to the `val orxFeatures` variable in  the `build.gradle.kts` file


```
import org.openrndr.extensions.NoClear

...
extend(NoClear())
```


### Command Line Arguments

Use [`kotlinx.cli`](https://github.com/Kotlin/kotlinx-cli) to allow for width/height, seed inputs, etc. Build up a standard set for easy re-use

#### Passing with gradle

```
./gradlew sketch -Ptitle=R01_CirclesAndTriangles --args="-w 500 -e 500 -s 0"
```

---

Tags: #tools