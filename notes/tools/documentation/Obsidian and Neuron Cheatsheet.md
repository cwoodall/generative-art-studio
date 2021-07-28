## Obsidian and Neuron Cheatsheet

- Regex for images from Obsidian to markdown:
  - Find `!\[\[([\w+\.\d-]+).(jpg|png|gif)\]\]`
  - Replace: `![](static/sketches/M01_SimpleMountains/$1.$2)`

- Embedding video example:
  ```html
  <embed src="static/sketches/M01_SimpleMountains/sketch.M01_SimpleMountains-2021-07-26-22.52.31.mp4" autostart="false" height="300" width="100%"></embed>
  ```
	- [[tools/documentation/espanso]] trigger added:
		- Copy to the clipboard what you want to embed as videio
		- type `:emb`
	- This is the related espanso entry:
	```yaml
	  - trigger: ":emb"
        replace: |
            <embed src="{{link}}" autostart="false" height="300" width="100%"></emb>
        vars:
          - name: "link"
            type: "clipboard"
     ```