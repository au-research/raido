### Use Spring Framework directly, without spring-boot.

* Status: final
* Who:  finalised by STO
* When: finalised on 2022-07-21
* Related: no related ADRs


# Decision

Use Spring Framework directly, without spring-boot.


### STO opinion 

Spring-boot is for building lots of little micro-services and/or getting 
started with Spring quickly.  
Raido doesn't need it -  we're already very familiar with Spring, and 
already going outside spring-boot's comfort zone (multiple schemas, multiple 
connection pools for queues, etc.)

Spring-boot has a different release cadence from Spring, and often lags in 
support of later versions of libraries.  It's a hassle co-ordinating spring-boot 
versions with everything else. 
For every library you include (and often just major upgrades) - you have to 
consider if/how it will work with spring-boot (spring-boot will often change 
behaviour or enable whole features just from adding a jar to the class path).

Spring-boot also increases the security surface area of the project - especially 
when you consider auto-configuration.

Spring-boot adds to startup time of the process

Anecdotal experience:
* Smallish medium sized project, > 100K lines of code
* intentionally didn't use Hibernate or other tech that does lots of startup 
  code generation
* removing spring-boot, using just spring, startup time went
  * from 6-ish seconds to 4-ish seconds on a devloper machine
  * 12-ish seconds to 10-ish seconds on a t3.micro 


# Context

The obvious choices are:
* Spring
* Spring Boot
  * used by cerium project

Not popular, small ecosystem, questionable future, etc:
* Quarkus
* DropWizard
* Micronaut


# Consequences

* spring-boot dominates searches
* integrating new techs needs more work than just letting spring-boot 
  auto-configure
* even spring itself is still quite complicated and has stuff we don't need
  * e.g. legacy java/spring as a html server (old MVC stuff, etc.)
  * this is where more modern, API-focused frameworks would be better, i.e
    Quarkus, Micronaut


# Links

* https://qr.ae/pvkz0n
  * STO: Only other article I could find to back me up on spring-boot.
    * This opinion is so rare, it *must* be good!  :|
  * Don't need links to tell you how great spring-boot is - just search, 
  they're all over the internet. Right next to the NoSQL articles.  :P




