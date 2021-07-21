## OpenRNDR

A [[Kotlin]] based framework for [[Creative Coding]]

### References

https://api.openrndr.org/
https://github.com/openrndr/orx

### Extensions

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

---

Tags: #tools