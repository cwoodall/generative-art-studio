## Quil

A framework for [[Creative Coding]] based on [[Processing]] and [[Clojure]], used by [[Tyler Hobbs]]. Use the [[lein]] script (which is included in this repository in `scripts/lein` which is added to the dev environment using [[direnv]]). One benefit is the fact that this uses [[Processing]] under the hood or [[P5.js]] for the web, which makes it easier to create interactive elements.

### Cheatsheet

#### New Project
```
lein new quil hello-quil
```

#### REPL

```
lein repl
```

Then from the REPL

```
(use 'my-art.core :reload-all)
```

#### Use Lein

-   add this to your project.clj ("name of your project + core"): `:main "hello-quil.core"`
-   add this to your `"projectname/src/core.clj"`: 
	-   `(defn -main [& args])`

and then you can run `lein run` !

### Quil with Clojurescript support

Supports closurescript which will generate a Javascript webpage which will use p5.js

```
lein new quil hello-quil
```

### Resources
- https://github.com/quil/quil/wiki/Installing

---

Tags: #tools 