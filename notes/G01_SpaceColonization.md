## G01_SpaceColonization

Using [[OpenRNDR]] to make a [[Space Colonization Algorithms]] to make some leaf like, tree like and maybe even different types of natural shapes and formation. Using [Jason Webb's](https://medium.com/@jason.webb/space-colonization-algorithm-in-javascript-6f683b743dc5) and the following papers ([1](http://algorithmicbotany.org/papers/venation.sig2005.pdf), [2](http://algorithmicbotany.org/papers/colonization.egwnp2007.large.pdf)) as a baseline. This involves having a bunch of nodes, these are the points that have already grown, and attractors (places which the nodes are growing towards). Each attractor then has a field of influence, for each of those nodes we calculate the average direction acting on the node. Draw the next segment, place our new nodes, and then finally kill off the attractors if a node get's to close to it (and repeat).

### The Algorithm

1. Place attractors
2. Associate the attractors with nearby nodes (within some **attraction distance**/**field of influence**)
3. Iterate through the nodes and grow the network (**segment length**)
4. Kill the attractors (**kill distance**)

