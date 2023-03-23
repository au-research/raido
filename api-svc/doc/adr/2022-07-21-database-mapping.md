### Use jOOQ

* Status: final
* Who:  finalised by STO
* When: finalised on 2022-07-21
* Related: no related ADRs


# Decision

Use jOOQ in preference to Hibernate/JPA, etc.

Hibernate/JPA/Spring-data can still be used if needed - they can co-exist 
reasonably well.


## STO opinion

We don't need/want cross-database compatibility.  Postgres is the choice - 
Raido is designed to work on Postgres, using PG-specific features.


* Raido is "schema-first" via flyway
* jOOQ allows us to write typesafe SQL queries instead of dealing with the 
  ORM impedance mismatch.  
* No stringly typed queries.  Refactoring is so much easier and safer.
  No real need for unit tests focusing solely on the DB mapping.
  TODO:STO link to coding guidelines, when I import them.
* jOOQ is much better at taking advantage of the PG-only approach
  * Hibernate/JPA are "lowest common denominator" approaches
* jOOQ favours performance by default
  * much less risk of people writing N+1 
  * reduced object-mapping overhead, mapping straight to flat arrays is a 
  first-class feature of jOOQ


# Context

Need a RDBMS mapping tech, writing JDBC by hand is not viable. 

The obvious choices are:
* jOOQ
* Hibernate
* Spring data JDBC/JPA
* QueryDSL


# Consequences

* jOOQ has now fully transitioned to being a commercial product
  * as such, their support for legacy database versions is limited
  * example: jooq 3.18.0 only supports postgres 15, but at the time I was 
    looking to upgrade jooq, AWS CDK does not yet support postgres 15 (RDS 
    does, just not the CDK yet for some reason)
  * we can't upgrade to a later version of jooq if it has dropped support 
    for whatever postgres version we're on
* people have to learn SQL
  * They always did, regardless of mapping tech chosen
  * "I don't want to learn SQL" is not an acceptable position on Raido.
* writing joins out explicitly is tedious/annoying
  * "always was" - if we were using HB/JPA, lazy loading would be disabled 
    * "stringly typed" HB/JPA querying causes havoc with eager loading 
    specifications when refactoring
  * there is some newer support for convenience lookups in jOOQ
    * but it increases probability of folks creating N+1 query problems :(
    * we'll probably disable it anyway (I assume can just switch off code 
    generation of that feature, like pojos and stuff.)




