## OpenRNDR

A [[Kotlin]] based framework for [[Creative Coding]]

[Forum](https://openrndr.discourse.group/)

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

#### ScreenRecorder / Video
https://openrndr.discourse.group/t/exporting-animated-gifs/134/2

GIFProfile: GIFs (low quality)
ProresProfile: mov (high quality)
MP4Profile: mp4 (medium quality)

```kotlin
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.videoprofiles.GIFProfile
import org.openrndr.extra.videoprofiles.ProresProfile
import org.openrndr.ffmpeg.ScreenRecorder

fun main() = application {
    program {
        extend(ScreenRecorder()) {
            profile = GIFProfile()
        }
        extend {
            drawer.clear(ColorRGBa.GREEN)
        }
    }
}
```


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