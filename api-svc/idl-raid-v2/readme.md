The OpenAPI definition files are in the [`/src directory`](./src).

The OpenAPI defintion is used to generate Java code for the 
`/api-svc/spring` project and
TypeScript for the `api-client` project.

Currently, we don't publish any kind of "swagger UI".


# Generating code from the spec

Generating code is done via [openapi-generator](https://github.com/OpenAPITools/openapi-generator)
using their [Gradle plugin](https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator-gradle-plugin/README.adoc).

Java code is generated from this project, via the Gradle 
[openApiGenerate](./build.gradle) task.

Typescript code for use in the UI is generated from the `app-client` 
[openApiGenerator](../../app-client/build.gradle) task.


# YAML maintenance burden 

We currently split out the YAML into many separate files and link them back 
into the root file via 
[`$ref` references](https://oai.github.io/Documentation/specification-components.html#the-reference-object).

This approach probably won't be appropriate when the API grows to have many 
endpoints, but it'll do for now.
 
IDEA and the openapi-generator tools seem to understand this `$ref` based
structure Ok, not sure about other tools.
We may need a "build" step to produce a single YAML file if the references turn
out to be problematic for other tools (e.g. swagger-ui or other documentation
generators).


## Long-term 

Consider writing our own DSL for generating the YAML file programmatically.
* not sure if we should use Gradle/Groovy/Java or TypeScript
  * I lean towards TS for good JSON support, and it's just a better scripting 
  language, but the build tooling is bad.
* not sure if we should commit the final YAML file or not
* don't do this until it's really needed, i.e. lots of YAML is really killing us
* wait for full openapi 3.1.0 support in our tooling

