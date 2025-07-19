# kibu-physics
A physics simulation for Minecraft: Java Edition, as a Fabric mod. 
It's largely just a fork of [Rayon](https://github.com/LazuriteMC/Rayon), which is licensed under the [MIT license](https://github.com/LazuriteMC/Rayon/blob/8356d464dd99e419023e3b3df4c13353611815aa/LICENSE), migrated to Minecraft 1.21.7+.
The mod uses the [Bullet Physics](https://github.com/bulletphysics/bullet3) engine to simulate physics with the Minecraft terrain and physics elements.

This mod doesn't do much on its own; it's mainly is a library for other mods to integrate with.

## Usage example
An example of how to integrate kibu-physics can be found in the test mod under `src/testmod`.