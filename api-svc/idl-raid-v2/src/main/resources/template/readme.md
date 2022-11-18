This directory contains template files used by openapi spring generator for 
generating code.

* The `original-6.0.1.api.mustache` file is the orginal template packaged in 
the 6.0.1 openapi-genreator release (extracted by hand).

* The `original-6.2.0.api.mustache` file was download from github:
https://github.com/OpenAPITools/openapi-generator/blob/24f476a38161a797c773577cab775ef285baeaba/modules/openapi-generator/src/main/resources/JavaSpring/api.mustache

* `6.2.1` 
  * https://raw.githubusercontent.com/OpenAPITools/openapi-generator/b0ce532bdc7436bf1ba377af5a72e5681565f2df/modules/openapi-generator/src/main/resources/JavaSpring/api.mustache
  * also pojo.mustache, for issue https://github.com/OpenAPITools/openapi-generator/issues/14059
    * I got this file by just cloning the repo, resetting to the release tag 
      and copying the file 

The specific file commit are obtained by using the "tags" page: 
https://github.com/OpenAPITools/openapi-generator/tags
Selected the tag for the release, follow the link to the commit, then click
"browse files", then navigate down to 
`/modules/openapi-generator/src/main/resources/JavaSpring/api.mustache`. 

Modifications:
* `api.mustache` - do not wrap endpoint return types in `ResponseEntity`
  * it's ugly and provides little value in modern Spring API definitions
* `pojo.mustache` - no not `@NotNull` annotation that causes compile error
