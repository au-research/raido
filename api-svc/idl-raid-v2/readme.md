The OpenAPI YAML file: [raid-v2-0-0.yaml](./src/raid-v2-0-0.yaml)

The file is used to generate Java code for the `/api-svc/spring` project and
TypeScript for the `acpi-client` project.

Currently, we don't publish any kind of "swagger UI".



# YAML maintenance burden 

That YAML file is going to get big over time and likely become a pain to 
edit and maintain.

## Short-term

Just suck it up and write the YAML. YAGNI - until you do 🤷‍.
IDEA helps a little bit with formatting validation, limited value validation 
and a bit of intellisense support for referencing things.


## Medium-term

Not sure if it's OpenAPI v3.0.0 or v3.1.0, but I think one of them lets you
split the one big file into separate ones that get `#ref` "included" into the
big file.  Need to investigate tooling support for that:
* openapi-generate, both java and ts
* whatever Swagger UI or equivalent we end using
* IDE


## Long-term 

Consider writing our own DSL for generating the YAML file programmatically.
* not sure if we should use Gradle/Groovy/Java or TypeScript
  * I lean towards TS for good JSON support, and it's just a better scripting 
  language, but the build tooling is bad.
* not sure if we should commit the final YAML file or not
* don't do this until it's really needed, i.e. lots of YAML is really killing us
* wait for full openapi 3.1.0 support in our tooling