
This directory is intended for use by client integration developers who are 
developing a new integration to the Raido API client.  

Expected integration scenarios:
* using the Raido API in an existing front-end client application
* integrating the Raido API to an existing back-end infrastructure
* creating a new front-end client application or website

The [onboarding-guide](./api-client-onboarding-guide.md) explains the process
of "business onboarding" - signing agreements and having your service-point 
created.  

The onboarding-guide also explains some basic concepts for Raido 
(environments, open/closed raids, etc.).

The [permission-model](./permission-model.md) explains the basics of how
raid data, service-points and api-tokens are related.

The [minting-a-raid](./minting-a-raid.md) guide walks through the process of
generating an api-token and calling the mint endpoint.

---

Further notes:

Client implementations should pay specific attention to the 
[xss.md](../security/api-svc/xss.md) topic, specifically: all clients 
implementations 
must perform output-encoding on all RAiD metadata fields.  

See the [access-control](../security/access-control) directory for information
about the `api-key`, `api-token` and other security oriented topics.

