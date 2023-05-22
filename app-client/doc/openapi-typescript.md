Context: generating TypeScript code from the Raido openapi YAML spec.

server.
We use [react-query](https://tanstack.com/query/v3/) to wrap the actual calls
to the api-svc. 

Would be nice if openapi generated react-query specific stuff for us, but it's
pretty simple to use them together. OTOH, any tool that directly binds openapi
and react-query together might be too opinionated.

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

We're just using the simple `typescript-fetch` generator to generate code based
directly on OpenAPI.  Then we wrap that in react-query calls because it's 
convenient (re-query on window focus, solid error and wait logic, etc. 
Maybe in the future, look at generating with Orval, just to see what's 
possible.

Very open to finding a better library / way.

Doco implies it doesn't support unions, but it does seem to work Ok. See the
various enum types in [metadata-block.yaml](../../api-svc/idl-raid-v2/src/metadata-block.yaml),
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

NPM library.

Not super popular, about 20K weeklies: https://www.npmjs.com/package/orval
Active, but sort of low: https://github.com/anymaniax/orval/pulse
Release history seems ok, a bit on the rapid side: https://github.com/anymaniax/orval/releases
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

