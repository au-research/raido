The OpenAPI YAML file: [raid-openapi-3.0.yaml](./src/raid-openapi-3.0.yaml)

The file is used to generate Java code for the `/api-svc/spring` project and
TypeScript for the `acpi-client` project.

Currently, we don't publish any kind of "swagger UI".



# YAML maintenance burden 

We currently split out the YAML into many separate files and link them back 
into the root file via 
[`$ref` references](https://oai.github.io/Documentation/specification-components.html#the-reference-object).
 
IDEA and the openapi-generator tools seem to understand these Ok, not sure 
about other tools.
We may need a "build" step to produce a single YAML file if the references turn
out to be problematic.


## Long-term 

Consider writing our own DSL for generating the YAML file programmatically.
* not sure if we should use Gradle/Groovy/Java or TypeScript
  * I lean towards TS for good JSON support, and it's just a better scripting 
  language, but the build tooling is bad.
* not sure if we should commit the final YAML file or not
* don't do this until it's really needed, i.e. lots of YAML is really killing us
* wait for full openapi 3.1.0 support in our tooling