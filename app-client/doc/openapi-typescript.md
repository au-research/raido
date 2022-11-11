Context: generating TypeScript code from the Raido openapi YAML spec.

I reckon react-query is the right tool for our needs with regard to calling the 
server.

Would be nice if it generated react-query stuff for us. OTOH, any tool that
does that might be too opinionated.

# Current tech selected 

## org.openapi.generator

https://openapi-generator.tech/docs/generators

Java library, part of the `org.openapi.generator` we already use.

Actually has a few generators that might be suitable:
* typescript-fetch
* typescript (experimental)
  * uses fetch, not sure how it's different from typescript-fetch
  * this is part of why wi might not want to use this library, terrible doco
* typescript-axios
* typescript-inversify
  * inversify is a IoC container library/framework thing
  * I've never really felt the need for IoC on client
    * but it does 600K weekly downloads?

Probably going to do the ts-fetch library to begin.  Then try to integrate it
manually into react-query to see if that's worth doing (I don't actually have
experience withe react-query, so probably better to start simples).  Maybe once
start learning react-query, look at generating with Orval, just to see what's 
possible.

Given that it's the same as our Java library, it probably doesn't support 3.1
Again, poorly documented 😒

Very open to finding a better library / way.

Doco implies it doesn't support unions, but it does seem to work Ok. See the
various enum types in [metadata-schema-v1.yaml](../../api-svc/idl-raid-v2/src/metadata-schema-v1.yaml),
the code generated for both TS and Java is fine.


# Considered

## @openapitools/openapi-generator-cli

https://www.npmjs.com/package/@openapitools/openapi-generator-cli

I think this is the official npm version of the gradle plugin?

It still appears to use Java under the hood, in that when I created an NPM
script like:
`"list-gen" : "./node_modules/.bin/openapi-generator-cli version-manager list"`
I got `Error: 'java' is not recognized as an internal or external command`.

## Orval

https://orval.dev/

NPM libary.

Not super popular, about 20K weeklies: https://www.npmjs.com/package/orval
Active, but sort of low: https://github.com/anymaniax/orval/pulse
Relase history seems ok, a bit on the rapid side: https://github.com/anymaniax/orval/releases
Supports OpenAPI 3.1.0: https://github.com/anymaniax/orval/issues/386
React-query support.


## openapi-typescript-codegen

https://github.com/ferdikoomen/openapi-typescript-codegen

NPM library.

75K weekly downloads: https://www.npmjs.com/package/openapi-typescript-codegen
Reasonable activity: https://github.com/ferdikoomen/openapi-typescript-codegen/pulse
Release history looks ok: https://github.com/ferdikoomen/openapi-typescript-codegen/releases
Supports OpenAPI 3.0, no issue for 3.1 support?
No React-query support out of the box.


# Not investigated

