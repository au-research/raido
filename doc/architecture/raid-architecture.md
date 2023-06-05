This is a high-level view of the architecture.
The diagram is a a mix of "current state" and "future state" as at 2023-02-06.

The Raido stuff exists and is "current state".
The Raid stuff does not exist yet; in the medium to long term, the components
of the "Raid system" will likely be managed in a separate repository to this
one. 

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
[technology-stack.md](../code/technol## How to mint a raid in DEMO environment
* sign-in via your chosen ID Provider
  * Google, AAF or ORCID
* request authorization for a service-point
  * Raid team creates service-point as part of biz onboarding
  * but you should send an email to `contact@raid.org` or otherwise notify
    us that you have submitted an authorization-request
  * add a comment that you would like admin access so that you can manage
    api-keys and generate api-tokens
* we will let you know when you can sign-in
* sign-in using the same ID provider you used before
  * authorization is linked to your ID Provider
* create an api-key
* generating an api-token
  * the api-token is to be considered sensitive, non-public information
    * it must be kept secret and should never be accessible to end-users
      * that is, do not embed the api-token in front-end applications or web-sites
    * the api-token is the only thing necessary to use the API and can be used
      to mint/edit raids and see closed raid data
  * save this somewhere safe, we do not store it in our system
    * but new ones can be generated at any time by just clicking the button
      again
* listing recent raids
  * call list raid with your api-token, that's it
* mint raid
* read a raid
  ogy-stack.md).


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
