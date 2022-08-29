This directory contains template files used by openapi spring generator for 
generating code.

The `original-6.0.1.api.mustache` file is the orginal template packaged in 
the 6.0.1 openapi-genreator release (extracted by hand).

Modifications:
* do not wrap endpoint return types in `ResponseEntity`
  * it's ugly and provides little value in modern Spring API definitions 