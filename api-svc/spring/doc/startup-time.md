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

`Server Started Server@72a7c7e0{STARTING}[11.0.9,sto=0] @1261ms`

### Commit 53dfbd3f

RaidV1 auth implementation on the `/v1/raid/ping` endpoint.

`Server Started Server@1700915{STARTING}[11.0.9,sto=0] @2053ms`

