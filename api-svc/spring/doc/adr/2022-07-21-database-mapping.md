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

We don't need/want cross-database compatibilty.  Postgres is the choice - 
Raido is designed to work on Postgres, using PG-specific features.


* Raido is "schema-first" via flyway
* jOOQ allows us to write typesafe SQL queries instead of dealing with the 
  ORM impedance mismatch.  
* No stringly typed queries.  Refactoring is so much easier and safer.
  No real need for unit tests focusing soley on the DB mapping.
  TODO:STO link to coding guidelines, when I import them.
* jOOQ is much better at taking advantage of the PG-only approach
  * Hibernate/JPA are "lowest common denominator" approaches
* jOOQ favours performance by default
  * much less risk of people writing N+1 


# Context

Need a RDBMS mapping tech, writing JDBC by hand is not viable. 

The obvious choices are:
* jOOQ
* Hibernate
* Spring data JDBC/JPA
* QueryDSL



# Consequences

* people have to learn SQL
  * They always did, regardless of mapping tech chosen
  * "I don't want to learn SQL" is not an acceptable position on Raido.


# Links

* https://qr.ae/pvkz0n
  * STO: Only other article I could find to back me up on spring-boot.
    * This opinion is so rare, it *must* be good!  :|
  * Don't need links to tell you how great spring-boot is - just search, 
  they're all over the internet. Right next to the NoSQL articles.  :P




