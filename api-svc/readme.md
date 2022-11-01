
# Introduction

The `api-svc` is a Spring application server that primarily serves the 
"V2" API: see [/api-svc/idl-raid-v2](/api-svc/idl-raid-v2).


# Architecture and deployment

The Raido api-svc is designed to operate on a "zero-downtime" basis.

That doesn't mean it will never be down (we support Raido on a "best effort",
in-hours basis), but the standard mode of operation and upgrade is designed
for zero downtime.

# Upgrading the api-svc

api-svc is intended to be completely stateless, running serveral compute nodes 
spread across separate physical datacenters.  New versions are rolled out
by adding nodes with the new version to the pool, and then draining the old
instances so that all versions are running the new software.

# Upgrading the database schema 

Schema migrations are carried out via Flyway, keeping backwards compatibility
between the new schema and the previous version of the api-svc so the two can
be deployed indepedently without either the api-svc or DB having to be taken 
offline.

# Upgrading / patching compute instances

Patching the compute instances is carried out the same way as upgrading the 
api-svc: new nodes are rolled across the pool until all instances are upgraded.

# Upgrading the Database instance

Raido uses AWS RDS to manage the Postgres database in a multi-az configuration.

When running minor database updates against this RDS configuration, AWS will
keep the databse available the same "rolling upgrade" process as the api-svc.

When running major database updates, there is usually some downtime - on the 
order of 10 minutes.  Downtime like this is scheduled to happen in the 
AWS RDS maintenance window: 
[/doc/service-level-guide.md](/doc/service-level-guide.md) 


# Running for local development

* setup the postgres DB as described in [db/readme.md](./db/readme.md)
* create the database schemas
  * run `./gradlew :api-svc:db:flywayMigrate`
  * the migration will create the `api_user`, but the user is disabled 
because it has no password
* connect to your local database and run the following to set a password
  * `alter user api_user password ''` (supply your own password)
* optionally, import the raid V1 data
  * see [v1-ddb-migration/readme.md](./db/v1-ddb-migration/readme.md)
* see [spring/readme.md](./spring/readme.md) for instructions on running the
  actual server, including configuring the password for the `api_user`