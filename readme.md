# Raido

"Raido" is the codename of the sourcecode for the new Oceania region 
[ARDC RAiD service](https://raid.org.au) implementation of 
[ISO 23527:2022](https://www.iso.org/standard/75931.html).

"RAiD" is the name of the ISO standard and overall project, including global
raid.org infrastructure and services not included in this repository.

"ARDC RAiD Service" is one instance of the Raido software, operated by the 
ARDC for the Oceania region.

See [raid-vs-raido.md](/doc/raid-vs-raido.md) for more explanation.


# Client API integrations

Folks looking to get started quickly integrating with the Raido API should
look at the [api-integration](/doc/api-integration/readme.md) guide.


# Environments

The production (`PROD`) environment is available at 
https://app.prod.raid.org.au.

There is also a `DEMO` environment for the RAiD service available at 
https://app.demo.raid.org.au.

Note that:
* you will not be approved to use either environment without prior
  agreement - send email to `contact@raid.org`
* the demo environment is under active development and is unstable 
  * the data gets reset frequently  

The [service-level-guide.md](/doc/service-level-guide.md) page provides guidance
to the expected level of Service provided by the RAiD team.


# Technology / Architecture

See [raid-architecture.md](./doc/architecture/raid-architecture.md) for a 
high-level view of the system.

There is also a guide to the 
[operational environment](/doc/architecture/environment/operational-environment.md) 
which outlines how we use AWS services for the operation of Raido.


## Project structure

* `/`
  * the root project contains no production code, it's just the container that
  holds all the other sub-projects
  * it does contain some build code though, see: [/buildSrc](./buildSrc)
* `/api-svc`
  * [/api-svc/db](/api-svc/db)
    * database schema definition, implemented as a series of Flyway migrations
  * [/api-svc/spring](/api-svc/spring) 
    * the main API server, implemented as a Spring application
  * [/api-svc/idl-raid-v2](./api-svc/idl-raid-v2/src/readme.md)
    * the OpenAPI definition of the API that the api-svc serves
  * [/api-svc/load-test](./api-svc/load-test/readme.md)
    * the load-tests and their results 
* [/raid-agency-app](/raid-agency-app/README.md)
  * The default Raido UI that uses api-svc 
* `/doc`
  * Various high-level technical documentation topics for Raido
  * [/doc/adr](/doc/adr/readme.md) 
    * Architecture decision log
  * [/doc/api-integration](/doc/api-integration/readme.md)
    * introductory documentation for folks who want to integrate directly with
    the Raido API
  * [/doc/architecture](/doc/architecture/raid-architecture.md)
    * an overview of the Raido architecture and our implementation in AWS
  * [/doc/code](/doc/code/readme.md)
    * coding standards/guidelines, etc.
  * [/doc/data-export](/doc/data-export/readme.md)
    * details about data exports
  * [/doc/development](/doc/development/readme.md)
    * details and guides for local development
  * [/doc/security](/doc/security/readme.md)
    * documentation and overview of the authentication and authorization 
    processes

# Development

Documentation about running locally, building and releasing can all be found
in [/doc/development](/doc/development). 

