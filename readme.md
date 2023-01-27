# Raido

"Raido" is the new implementation of the Oceania region 
[RAiD](https://raid.org.au) implementation.

It is not yet live, though you can find the demo environment at 
https://demo.raido-infra.com.  

Note that:
* you will not be approved to use the demo environment without prior agreement
* the demo environment is under active development and is very unstable 
  * the data gets reset frequently (weekly, sometimes daily) 

Note that previously, the repo was named `raido-v2`, but that has been 
[fixed](https://github.com/au-research/raido/issues/4). 

The [service-level-guide.md](/doc/service-level-guide.md) page provides guidance
to the expected level of service provided by the Raido team.

# Technology / Architecture

A C4 container diagram for the basic architecture can be found in 
[raido-container-c4.md](./doc/architecture/raido-container-c4.md).

The overall technology stack is described in the
[technology-stack.md](/doc/technology-stack.md) page.


Important architectural decisions are recorded in the 
[Architecture Decision Log](https://github.com/joelparkerhenderson/architecture-decision-record#what-is-an-architecture-decision-record).

* [/doc/adr](./doc/adr)

Different sub-projects can have their own ADR log, in their local /doc/adr
directory.

* [/app-client/doc/adr](./app-client/doc/adr)
* [/api-svc/doc/adr](./api-svc/doc/adr)


## Project structure

* /
  * the root project contains no production code, it's just the container that
  holds all the other sub-projects
  * it does contain some build code though, 
  see: [/buildSrc](./buildSrc)
* [/api-svc](/api-svc)
  * [/api-svc/db](/api-svc/db)
    * database schema definition, implemented as a series of Flyway migrations
  * [/api-svc/spring](/api-svc/spring) 
    * the main API server, implemented as a Spring application
  * [/api-svc/idl-raid-v2](./api-svc/idl-raid-v2/src/readme.md)
    * the OpenAPI definition of the API that the api-svc serves
* [/app-client](/app-client)
  * The Raido UI that calls uses api-svc 


# CI

Using Github actions at the moment to run build, test and quality tasks.

These generally execute on push to to `main` or on pull-requests.
So if you want to test stuff in Github without pushing to `main`;
  work in your own branch, create a PR and it will run the build and tests.
Note that changes consisting solely of `*.md` files are intended to be ignored.

* [api-svc-ci.yml](.github/workflows/api-svc-ci.yml)
  * builds and runs unit and integration tests
* [app-client-ci.yml](.github/workflows/app-client-ci.yml)
  * does a production build of the app-client
* [codeql-analysis.yml](.github/workflows/codeql-analysis.yml)
  * runs an api-svc build in the context of Github 
  [codeql](https://github.com/github/codeql-action)

* Look in [Github Actions](https://github.com/au-research/raido/actions)
  console to see what's going on


# Development 

Pre-requisites and instructions for 
[local development](./doc/local-development.md) 
 
## Coding standards

See [/doc/code](./doc/code/readme.md) - there may also be further sub-project 
specific standards local to that project, look in the local `/doc` directory.

## Building
See the relevant local readme for building api-svc, api-svc/db and app-client.

See [build-troubleshooting.md](/doc/build-troubleshooting.md) if
having issues.

