## R-Tree

Similar to a [[techniques/Spatial Indexing/QuadTree]], but uses a different definition for its sub boxes:

https://www.geeksforgeeks.org/introduction-to-r-tree/

**Comparison with Quad-trees**:

-   Tiling level optimization is required in Quad-trees whereas in R-tree does not require any such optimization.
-   Quad-tree can be implemented on top of existing B-tree whereas R-tree follow a different structure from a B-tree.
-   Spatial index creation in Quad-trees is faster as compared to R-trees.
-   R-trees are faster than Quad-trees for Nearest Neighbor queries while for window queries, Quad-trees are faster than R-trees.

