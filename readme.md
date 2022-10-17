# raido-v2

"Raido" is the new implementation of the Oceania region 
[RAiD](https://raid.org.au) implementation.

It is not yet live, though you can find the demo environment at 
https://demo.raido-infra.com.  

Note that:
* you will not be approved to use the demo environment without prior agreement
* the demo environment is under active development and is very unstable 

It's called "v2" because initially it was called raid-v2 because there was a
v1. Should've just called it "raido" - oh well.

Important architectural decisions are recorded in [/doc/adr](./doc/adr).
Different sub-projects have their own ADR log, in their local /doc/adr
directories (db, app-client, [api-svc](/api-svc/doc/adr), etc.)

The overall technology stack can be found in the
[technology-stack.md](/doc/technology-stack.md) page.

The [service-level-guide.md](/doc/service-level-guide.md) page provides guidance
to the expected level of service provided by the Raido team.


# Project structure

* /
  * the rooot project contains no production code, it's just the container that
  holds all the other sub-projects
  * it does contain some build code though, 
  see: [/buildSrc](./buildSrc)
* [/api-svc](/api-svc)
  * [/api-svc/db](/api-svc/db)
    * database schema definition, implemented as a series of Flyway migrations
  * [/api-svc/spring](/api-svc/spring) 
    * the main API server, implemented as a Spring application
  * [/api-svc/idl-raid-v2](./api-svc/idl-raid-v2/src/raid-openapi-3.0.yaml)
    * the OpenAPI definition of the API that the api-svc serves
* [/app-client](/app-client)
  * The Raido UI that calls uses api-svc 


# CI

Using a Github action at the moment to run tests.

* [CI - gradle.yml](.github/workflows/gradle.yml)
  * builds and runs unit and integration tests
  * executes on push to to `main` or on pull-requests
    * So if you want to test stuff in Github without pushing to `main`;
      work in your own branch, create a PR and it will run the build
      and tests.
  * Changes to `*.md` files are not built.

* Look in [Github Actions](https://github.com/au-research/raido-v2/actions)
  console to see what's going on


# Development pre-requisites

* [JDK 17](./doc/adr/2022-07-21_jdk-platform.md)
* [Node.js 16](./doc/adr/2022-07-21_nodejs-platform.md)
* Docker
  * postgres 14 
  

# Building
See the relevant local readme for building api-svc, api-svc/db and app-client.

See [build-troubleshooting.md](/doc/build-troubleshooting.md) if
having issues.

