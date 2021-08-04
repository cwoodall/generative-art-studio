# QuadTree

QuadTree is an efficient way to look up the locations of all particles inside of a 2-dimensional space.

Question: Just give me everything in this region of the QuadTree (OctTree/K-tree for dealing with n-dimensional space)

Motivation: if you don't do this you would need to check EVERY particle (N) in relationship to every other particle (N) making this $O(N^2)$ if used neively

Lookup is O(log(n)) every particle in relationship to every other particle is O(n). So the overall relationship O(n log(n))

For each frame of animation you need to rebuild the "QuadTree", why? as particles move around the page you end up needing to recompute

Need a concept of a `Node` or a `Point`.


## Core concepts

- insert
- subdivide
- query

Insert, inserts a point in the current QTree/boundary, if it is at full capacity it subdivides into equal regions and pushes the point into the subtree. This happens recursively

Query goes through and finds the points inside of a range

subdivide creates a new set of subtrees.


## Uses

This could also be a way to not use shaders for [[C02_ColorStudy_2dcity]].

For making an [[techniques/Space Colonization Algorithms]]


## Implementations

[[OpenRNDR]] offers a few options. 
-	[`orx-kdtree`](https://github.com/openrndr/orx/blob/c1cb383625046398c44b5a354d17129e5dd9656c/orx-kdtree): Find the closest point
-	https://github.com/openrndr/orx/tree/c1cb383625046398c44b5a354d17129e5dd9656c/orx-quadtree: Find all points within some radius