This is a high-level view of the architecture.

The Raido system is operated by the ARDC (as the "ARDC RAiD Service") in our 
role as one of many regional registration-agencies for the RAiD standard.

The Raid system is operated by the ARDC in our role as the singular 
registration-authority for the RAiD standard. 

The Raid system is currently in "prototype" stage, it is managed out of a separate 
repository: https://github.com/au-research/raid-aws-private

The Lambda implementation for the redirect functionality is a stop-gap measure, 
it was the simplest way to get something up and running.  It is envisioned that
in the future, as the global raid infrastructure becomes more complicated - it 
will likely be re-written in a technology stack similar to raido so that it 
is suitable for operation, maintenance and support by the ARDC.

At the moment, the raid infrastructure supports resolving a raid:
* if using a browser, it will redirect to the anonymous landing page of the 
  registration-agency for that raid
* if using an API (e.g. curl request) it will redirect to the anonymous resolve 
  endpoint of the raid-agency for that raid
* the logic to choose the registration-agency landing page is based on the raid 
  handle prefix

See the [raid-vs-raido.md](/doc/raid-vs-raido.md) for a description of 
the difference between Raid and Raido.


# Architecture
<!--- Note the `?cache=no` param --->
![PlantUML model](https://www.plantuml.com/plantuml/png/9Sl13G8n34JHErL00OaldEZ6E1Qnb3WhPopQPrTSvyr_cCl8fXdZte5ZluY2l_LZwFdEhI7BeOugQn9d2TtA8VryMLiqsPpQ4hesWmeoz6_bAa_MAFAins17pd7x0G00?cache=no)

**Note**: There is no current plan to implement a global RAiD "mint" 
endpoint.

The only "central RAiD API" exists for the convenience of _readers_ who do not 
need/want to deal with the federated nature of the RAiD system.

In order to mint a RAiD, a customer must have a direct legal relationship 
with an organisation operating as a RAiD registration agency.  The customer 
may then mint their RAiDs by using the API or App made available to them by
the registration agency, secured by an appropriate mechanism (`api-key`, etc.)

Oceania RAiD clients will mint their RAiDs via Raido after establishing 
their relationship with the ARDC.

It is possible the global RAiD infrastructure could be expanded in the 
future to support a global "mint" endpoint.  But that would depend on organising
all RAiD agencies to provide a commitment to support the requirement and 
it's availability needs (an organisational challenge) and securing funding to 
maintain/operate it on an on-going basis (a financial challenge).  


Major architectural decisions are documented in the Architecture Decision Log:
* [high level](../adr)
* [api-svc](../../api-svc/doc/adr)
* [app-client](../../app-client/doc/adr)


# Technology

More info about individual technology choices can be found in 
[technology-stack.md](../architecture/technology-stack.md).

## How to use the DEMO environment

See the [api-integration guide](/doc/api-integration/readme.md) for the 
pre-requisites and a guide to using the DEMO system.


# Operational environment

The ARDC acts as a registration-agency for the Oceania region.  

The ARDC runs the Raido software as the "ARDC RAiD Service", intended for use
by organisations in the Oceania region.

https://ardc.edu.au/services/ardc-identifier-services/raid-research-activity-identifier-service/

This service is operated mostly in AWS, see 
[operational-environment.md](./environment/operational-environment.md)
for a description of how we map the architecture as shown onto AWS services.

----

# PlantUML technical information  


The diagram above is a 
[C4 container diagram](https://en.wikipedia.org/wiki/C4_model), 
generated using [PlantUML](https://plantuml.com/).
The source for this diagram is at
[raido-container-c4.puml](./raido-container-c4.puml).
It is dynamically generated (from the `main` branch), via the plantuml.com 
public web service, see this 
[StackOverflow example](https://stackoverflow.com/a/32771815/924597).

Note that we are only just learning about C4 modelling and the diagram above
likely mis-uses the C4 constructs - this diagram is very much a work in 
progress.

Maintainers: there's a decent 
[PlantUML plugin](https://plugins.jetbrains.com/plugin/7017-plantuml-integration) 
to help with writing diagrams, 
note that it is not maintained by JetBrains.
Also, I think non-Windows users may have to install PlantUML locally.
