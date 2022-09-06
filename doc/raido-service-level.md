**Note**: This page is a prototype, it is not yet agreed that these are the 
usage terms for Raido.

It is intended that we will "publish" these terms by adding a link from the new
"about RAID" page to this page on github.

----

Raido is just one possible implementation of the RAiD ISO standard.
Other registration agencies may run RAiD implementations with completely
different levels of service.

The below service level statements are in the nature of "aspirations" for the  
Raido system - they are not a guarantee and this document is not intended as a 
contract.

This page is published in order to set expectations around how Raido can be
used and what should be expected as baseline level of service.  

The Raido team aims to provide this level of service on a best-effort basis, 
subject to funding and resource availability.


# Availablity

* 24 / 7 operation on a "best effort" basis
* "business-hours" support only - AEST (GMT +10) only

# Maintenance window

There are multiple separate maintenance windows for AWS and Raido managed 
resources.

## AWS maintenance windows

Most AWS resource maintenance is carried out in a "zero downtime" fashion, but 
users may experience degraded performance while the upgrade is in progress.

### Database backup window

Daily: 11:00 - 11:30 UTC (23:00 - 23:30 AEST)

Users may experience slightly degraded performance during the backup window.

### Database maintenance window 

Weekly: Sunday 12:00 - Sunday 13:00 UTC (Monday 00:00 - 01:00 AEST)

Users may experience degraded performance during the maintenance window.

Occasionally, the Raido system may return HTTP 500 errors during the 
maintenance window. The Raido team will make best efforts to communicate 
in advance when these outages are expected.

## Raido maintenance window

Weekly: Tuesday 01:30 - 02:00 UTC (Tuesday 11:30 - 12:00 AEST)

Where possible, Raido maintenance is carried out with zero downtime, but
performance may be degraded during the maintenance window.

Occasionally, the Raido system may return HTTP 500 errors during the
maintenance window. The Raido team will make best efforts to communicate
in advance when these outages are expected.


# Usage limits 

In order to ensure availability and fairness to all institutions, the API 
service has limits for various things.

* 5 mint requests per second, per institutuion
  * requests in excess of the limit may be rejected with HTTP 429 errors
* 25 general API requests per second, per institution
  * requests in excess of the limit may be rejected with HTTP 429 errors
* RAID DMR is limited to 200 KB maximum size


# API Compatibility 

## Stable endpoints

API endpoints marked "stable" are intended to be supported in the long-term.
However, sometimes it is necessary to remove old API endpoints so that the
Raido team can maintain the system effectively.

Where it is deemed necessary, specific versions of an API endpoint may be
marked "deprecated". The users of that specific endpoint version will
be notified, along with instructions on which stable endpoints to use in 
future.

The deprecation period will last at least 18 months - at the end of the 
deprecation period the endpoint may, with notice, be removed completely 
from the API.

Institutions using the API are expected to make best efforts to upgrade their
integration with the API within the deprecation period.

Wher an institution cannot upgrade their integration and needs the deprecation
window to be extended, they are expected to communicate with the Raido support
team as soon as possible.


## Experimental endpoints

When developing new UI and other features, new endpoint versions will be 
added to the API without notice.  These endpoints are marked as "experimental".

Experiment endpoints are intended to enable rapid prototyping and evolution of
new RAID features.  Experimental features are not supported for use by 
institutions - they may change shape, change behaviour or be deleted without 
notice.