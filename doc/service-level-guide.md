This service level guide will be published by linking to it from the 
[ARDC Product Service Versioning standard(https://confluence.ardc.edu.au/display/DPST/ARDC+Online+Services%3A+Introduction+of+Maintenance+Windows+and+Product+Versions)
 
The link above will be updated when the standard is published.

----

The ARDC RAiD service is just one possible implementation of the RAiD ISO 
standard registration-agency regional service functionality.
Other registration agencies may run RAiD implementations with completely
different levels of service.

The below service level statements are in the nature of "aspirations" for the
RAiD Service - they are not a guarantee and this document is not intended as a 
contract.

The service level guide is published in order to set expectations around how 
the service can be used and what can be assumed as a baseline level of 
support.  

The RAiD team aims to provide this level of service on a best-effort 
basis, subject to funding and resource availability.

The service level guide may change, if we find we can't meet this level of 
service, we will adjust the guide to provide guidance on the service level
we are able to achieve. New versions of the service level guide will be
published at this location, previous versions of the document are available
in the Git history.


# Availability

* 24 / 7 operation on a "best effort" basis
* "business-hours" support only - AEST (GMT +10) only

# Maintenance window

There are multiple separate maintenance windows for various AWS resources and 
the RAiD service itself.

## AWS maintenance windows

Most AWS resource maintenance is carried out in a "zero downtime" fashion, but 
users may experience degraded performance while the upgrade is in progress.

### Database backup window

Daily: 11:00 - 11:30 UTC (23:00 - 23:30 AEST)

Users may experience slightly degraded performance during the backup window.

### Database maintenance window 

Weekly: Sunday 12:00 - Sunday 13:00 UTC (Monday 00:00 - 01:00 AEST)

Users may experience degraded performance during the maintenance window.

Occasionally, the RAiD service may return HTTP 500 errors during the 
maintenance window. The RAiD team will make best efforts to communicate 
in advance when these outages are expected.

## RAiD service maintenance window

Weekly: Tuesday 01:30 - 02:00 UTC (Tuesday 11:30 - 12:00 AEST)

Where possible, RAiD service maintenance is carried out with zero downtime, but
performance may be degraded during the maintenance window.

Occasionally, the RAiD service may return HTTP 500 errors during the
maintenance window. The RAiD team will make best efforts to communicate
in advance when these outages are expected.


# Usage limits 

In order to ensure availability and fairness to all institutions and 
customers of the system, the API service has limits for various things.

* 5 mint requests per second, per service-point
  * requests in excess of the limit may be rejected with a HTTP 429 error
  * requests that try to associate too many PIDS (> 10) with a raid in a single 
    request may be rejected with a HTTP 400 error
    * because we validate every PID's existence with the relevant external 
      system (ORCIRD, DOI, etc.) - validating many PIDs in a single request
      may be deemed too expensive
    * when this happens, we ask that you break the request into multiple 
      separate update requests
    calls
* 25 general API requests per second, per service-point
  * requests in excess of the limit may be rejected with a HTTP 429 error
* RAID Metadata is limited to 200 KB maximum size
  * requests to mint or update records with a metadata size of > 200 KB may be 
    rejected with a HTTP 413 error

Note that these limits may not initially be enforced, and when they are 
enforced it's likely the limits will be set with some leeway.  Being able to 
achieve a higher throughput than those documented should be taken as evidence
that the higher throughput level will always be available.

Furthermore, the documented throughput limits are "best effort" - if the 
service comes under usage pressure, the endpoints may begin returning 429 
errors even though your usage is within the throughput limits indicated.


# API Compatibility 

## Stable endpoints

API endpoints marked `stable` are intended to be supported in the long-term.
However, sometimes it is necessary to remove old API endpoints so that the
RAiD team can maintain the system effectively.

Where it is deemed necessary, specific versions of an API endpoint may be
marked `deprecated`. The users of that specific endpoint version will
be notified, along with instructions on which stable endpoints to use in 
future.

The deprecation period will last at least 18 months - at the end of the 
deprecation period the endpoint may, with notice, be removed completely 
from the API.

Institutions using the API are expected to make best efforts to upgrade their
integration with the API within the deprecation period.

Where an institution cannot upgrade their integration and needs the deprecation
window to be extended, they are expected to communicate with the RAiD team as 
soon as possible.


## Experimental endpoints

When developing new UI and other features, new endpoint versions will be 
added to the API without notice.  These endpoints are marked as `experimental`.

Experiment endpoints are intended to enable rapid prototyping and evolution of
new RAID features.  Experimental features are not supported for use by 
institutions - they may change shape, change behaviour or be deleted without 
notice.