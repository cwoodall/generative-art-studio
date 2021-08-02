## C03_ShaderExperiment

The goal here was to get the basic shaders to work, in order to do this I had to learn a little about shaders (which I found semi non-intuitive and the documentation is poor at best).

### Algorithm

- Create a renderTarget with a depth and color buffer
- Create your shapes and color index assignments into the palette
- Each iteration
	- Clear the buffer
	- Draw your shapes with the shader
	- Update your world state
	-draw the render target to the screen
	
The shader looks at each pixel matches its color against the palette, does the mod math from [[C01_ColorStudy_AddingIndex]] and [[C02_ColorStudy_2dcity]] and then outputs the new color to that pixel. See [[sketches/C01_ColorStudy_AddingIndex#The Wrapped Color Palette Math]] for details on the color palette 

#### The Shader

Take the current color buffer state for the render target (the current image) as a texture. Then within that texture find where the present fragment is. After that we can modify the state by returning a new color for that fragment.

[[Shaders]] in [[OpenRNDR]] use a combination of [[GLSL]] ([[OpenGLs]] Shader language) and a few conventions around making fragment and vertex shaders. Vertex shaders basically determine how shape vertexes and triangles get mapped to the final displayed image. Fragment or pixel shaders, determine how the individual pixels are rendered.

To get this to work I created the following class which implements a fragment shader `shadeStyle`:

```kotlin
open class ShaderColorPaletteModMath(
  colorBuffer: ColorBuffer,
  width: Int,
  height: Int,
  colorIdx: Int,
  palette: BasePalette
) : ShadeStyle() {

  var colorBuffer: ColorBuffer by Parameter()
  var width: Double by Parameter()
  var height: Double by Parameter()
  var colorIdx: Int by Parameter()
  var palette: Array<Vector4> by Parameter()

  init {
    this.colorBuffer = colorBuffer
    this.width = width.toDouble()
    this.height = height.toDouble()
    this.colorIdx = colorIdx
    this.palette = palette.toVector4Array()

    fragmentTransform = """
            vec2 invSize = vec2(1/p_width, 1/p_height);
            // Reflect the axis so that the upper left hand corner is 0,0
            vec2 coord = vec2(0,1) - c_screenPosition.xy*invSize * vec2(-1,1);
			
			// Load the current screen state as a 2d texture
            vec4 c = texture2D(p_colorBuffer, coord);
			
			// find the color index represented by the current color, if it
			// is not found we will color over it unless it is black.
            int bg_idx = 0;
            for(int i = 0; i < p_palette_SIZE; i++) {
              if (c.rgb == p_palette[i].rgb) {
                bg_idx = i;
                break;
              } else if (c.rgb == vec3(0.0, 0.0, 0.0)) {
                bg_idx = -1;
                break;
              }
            }
            
            if (bg_idx >= 0) {
              int next_idx = (p_colorIdx + bg_idx) % p_palette_SIZE;
              x_fill.rgb = p_palette[next_idx].rgb;
            } else {
              x_fill.rgb = c.rgb;
            }
            
            // Setting the stroke alpha to 1 prevents the weird smoothing effect that was causing issues at the
            // edges
            strokeAlpha = 1.0;
            """.trimMargin()
  }
}
```

This is a shader which uses the present screen color buffer as a texture. To get the pixel lookup to line up I had to scale and reflect the cordinate position so that the upper left hand corner is (0.0, 0.0) and the bottom right hand corner is (1.0, 1.0). Once doing this this texture lookup works properly. 

Afte this we search through the palette to match the present color agains the background color. This will allow us to find the next index in the palette for the current color.

At the end I set `strokeAlpha = 1.0`, which seems to prevent some weird effects at the edges of the stroke our outlines placed on colors, likely due to mixing the stroke or color into the shape.

To improve this I would use a texture that represented the indexes directly with no aliasing or anything, then rendered the colors from that using 2 different buffers. I think this would avoid all of the weird aliasing issues I have been running into, since the color boundaries would always be clearly defined.

### Progression

#### Trying to understand how the shader and textures work
![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-00.56.46.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-00.56.46.png)

![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-01.22.28.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-01.22.28.png)

![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-01.41.38.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-01.41.38.png)

![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-08.59.45.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-08.59.45.png)

#### Applying a nicer color palette
![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-09.27.49.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-09.27.49.png)

![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-09.27.53.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-09.27.53.png)

![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-09.28.00.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-09.28.00.png)

<embed src="static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-09.44.39.mp4" autostart="false" height="300" width="100%"></embed>

#### Porting over C02

![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-10.19.58.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-10.19.58.png)

![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-10.20.02.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-10.20.02.png)

![static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-10.30.09.png](static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-10.30.09.png)

<embed src="static/sketches/C03_ShaderExperiments/sketch.C03_ShaderExperiment-2021-07-31-10.26.19.mp4" autostart="false" height="300" width="100%"></embed>

Having some issues with outlines etc

#### Getting rid of the outlines

To get rid of the outlines simply setting the `strokeAlpha = 1.0` at the end of the shader seemed to help the most. I also had to subtract 2 pixels from the height and somewhere between 1 and 2 pixels from the width depending on the dx of the rhombus... I have not yet quite figured this out yet.


#### References

- https://guide.openrndr.org/#/06_Advanced_drawing/C04_Shade_styles?id=usage-examples
- https://learnopengl.com/Getting-started/Textures
- https://thebookofshaders.com/11/

I had to look at the `ShaderError.txt` output a lot to get an idea of what parameters are available to me

the sampler2d is still a bit of a mystery

https://medium.com/@grahamte/glsl-shaders-and-the-magic-of-graphical-rendering-ca94a578cf8b

### Future Work

- Shader still has issues around the outline boundaries. Maybe don't draw them then draw all of the outlines in bulk at the end if I want them