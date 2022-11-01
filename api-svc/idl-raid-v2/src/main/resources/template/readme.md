This directory contains template files used by openapi spring generator for 
generating code.

The `original-6.0.1.api.mustache` file is the orginal template packaged in 
the 6.0.1 openapi-genreator release (extracted by hand).

The `original-6.2.0.api.mustache` file was download from github:
https://github.com/OpenAPITools/openapi-generator/blob/24f476a38161a797c773577cab775ef285baeaba/modules/openapi-generator/src/main/resources/JavaSpring/api.mustache

The specific file commit was chosen by using the "tags" page: 
https://github.com/OpenAPITools/openapi-generator/tags
I selected the 6.2.0 release, followed the link to the commit, then navigated 
down to 
`/modules/openapi-generator/src/main/resources/JavaSpring/api.mustache`. 

Modifications:
* do not wrap endpoint return types in `ResponseEntity`
  * it's ugly and provides little value in modern Spring API definitions
* do not add `@RequestMapping` to the interface
  * 6.2.0 added an `@RequestMapping` at the class level to implement default
    base path mapping.  But that causes the IntTest FeignClient to barf, because
    the feign folks consider that to be a "security issue":
    https://github.com/spring-cloud/spring-cloud-openfeign/issues/698#issuecomment-1207841204

