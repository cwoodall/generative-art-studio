## C03_ShaderExperiment

The goal here was to get the basic shaders to work, in order to do this I had to learn a little about shaders (which I found semi non-intuitive and the documentation is poor at best).


### Algorithm

- Render a background to a render target with one colorBuffer

extract that color buffer and pass it as a texture to a shader style to draw more new shapes and modify the underlying shapes based on this.

### References

- https://guide.openrndr.org/#/06_Advanced_drawing/C04_Shade_styles?id=usage-examples
- https://learnopengl.com/Getting-started/Textures
- https://thebookofshaders.com/11/

I had to look at the ShaderError.txt output a lot to get an idea of what parameters are available to me

the sampler2d is still a bit of a mystery

https://medium.com/@grahamte/glsl-shaders-and-the-magic-of-graphical-rendering-ca94a578cf8b

### Future Work

- Shader still has issues around the outline boundaries. Maybe don't draw them then draw all of the outlines in bulk at the end if I want them