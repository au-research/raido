# Raido

"Raido" is the codename of the sourcecode for the new implementation of the 
Oceania region [ARDC RAiD service](https://raid.org.au) implementation for 
[ISO 23527:2022](https://www.iso.org/standard/75931.html).

It replaces the now decommissioned 
https://github.com/ResearchDataServices/RAiD-API.


## `Raido` vs `RAiD Service` vs `RAiD`

When you see these terms in this codebase:
* `Raido`, `raido` - refers to the software codebase that implements
  our "local" RAiD service for the Oceania region.
  * It's a "codename" for the software project and not intended to be publicly 
  visible.  
  * The term should not be used in the UI or other user-visible places.
* `RAiD service` - in customer/user communications, we always refer to the 
  "RAiD service" or "ARDC RAiD service" when talking about the service we 
  provide to our customers that is based on the Raido software (hosted in AWS).
  * The `RAiD service` is operated and maintained by the ARDC in our
    capacity as one of the (hopefully many) "registration agency" providers.
  * The `RAiD service` is located in and primarily intended for customers in 
    the Oceania region. 
* `RAiD`, `RAID`, `Raid`, `raid` - refers to the "global" RAiD ISO standard and 
  supporting global infrastructure (i.e. `raid.org`), being operated and 
  maintained by ARDC in our capacity as the ISo standard "registration 
  authority".
  * note that `raid.org` does not exist yet, and when it does, the software will
  live in a separate repository


There is a DEMO environment for the RAiD service available at 
https://app.demo.raid.org.au.

Note that:
* you will not be approved to use the demo environment without prior agreement
* the demo environment is under active development and is unstable 
  * the data gets reset frequently  

Note that previously, the repo was named `raido-v2`, but that has been 
[fixed](https://github.com/au-research/raido/issues/4). 

The [service-level-guide.md](/doc/service-level-guide.md) page provides guidance
to the expected level of the RAiD Service provided by the RAiD team.


# Technology / Architecture

See [raid-architecture.md](./doc/architecture/raid-architecture.md).

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


## Development and Release Branching

Currently daily development, DEMO and PROD builds and releases are all done 
using the `main` branch.

Releases are tracked and built using `git describe` functionality working off
of annotated tags with the prefix `raido-v-`.

[release-process.md](./doc/release-process.md)

AWS build and deployment is automated via AWS CodeBuild projects - see the 
(private) [raido-v2-aws-private](https://github.com/au-research/raido-v2-aws-private) 
repo for details.

