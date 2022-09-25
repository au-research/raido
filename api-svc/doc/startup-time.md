This document is intended to show the history of the startup time of the 
api-svc.

Raido aims for a fast startup, so we can cycle versions/EC2 instances as 
quickly as possible.  Specifically, the focus on start time is  not about 
quick integration testing or other DX concerns - it's about production.

Times from a dev machine aren't realistic - should redo these tests when
we have AWS infrastructure setup.


## STO machine

* Ryzen 5 3600 (6 cores)
* 32Gb RAM
* Samsung SSD 980 Pro

Time displayed by the Jetty "Server Started" message, with a few warmup starts.


### Commit f2653f35

Just the `/public/status` endpoint, no real security, no JOOQ, etc.

Without building infrastructure to cache JVM / Spring startup - this is about
as quick as Spring can be booted.

STO machine: `Server Started Server@72a7c7e0{STARTING}[11.0.9,sto=0] @1261ms`


### Commit 53dfbd3f

RaidV1 auth implementation on the `/v1/raid/ping` endpoint.

STO machine: `Server Started Server@1700915{STARTING}[11.0.9,sto=0] @2053ms`


### Commit 501dfe2a - 2022-08-28
 
`/v1` RAID endpoints and `/idpresposne` authn endpoint

STO machine: `Server Started Server@4d9e68d0{STARTING}[11.0.9,sto=0] @2276ms`
t3.micro: `Server Started Server@19bb07ed{STARTING}[11.0.9,sto=0] @4459ms`


