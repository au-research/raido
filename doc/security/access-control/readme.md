The terminology used here generally aligns with the wikipedia topic:
https://en.wikipedia.org/wiki/Computer_access_control

# `authentication`

* refers to "the act of proving an assertion, such as the
  identity of a computer system user."  Where "identity" is a fairly
  unstructured concept for us (e.g. a user authenticating vai ORCID may choose
  not to make any identifying information available to us - no name, no
  email, just the ORCID ID itself).
* see [authentication](./authentication)


# `authorization`

* refers to "the function of specifying access rights/privileges to  
  resources"
  * see [authorization](./authorizaton)
* older documentation sometimes refers to the concept of an "authz-token"
  which is used interchangeably with the concept of an "api-token"


# `accountability` AKA `access authorization` 

Currently all authenticated access to the system is gated through a manual 
approval process involving a human representative of either the Raid service 
team (an `OPERATOR`) or of the service-point (an `SP_ADMIN`).


## Humans

* a human user authenticates via their chosen SSO method (AAF, ORCID,
  etc.) then requests access to a designated service-point
* an `SP_ADMIN` or `OPERATOR` then approves (or denies) the request and the  
  person can then use the system to mint/edit/view raids associated with 
  that service-point

## Machines

* an `SP_ADMIN` or `OPERATOR` creates an api-key
  * the "identity" of the key serves to document and indicate the intended 
    usage context of the api-key
* an `SP_ADMIN` or `OPERATOR` then generates an api-token, which they then must
  copy and store somewhere
  * a new api-token can be generated at any time
  * when the api-key is disabled, all generated api-tokens are disabled 
* the client integrator must then send the api-token in the `Authorization` 
  header of each API request (api-token value must be prepended with "Bearer ")


# `access auditing`

Raido currently has no auditing support  - there is currently no record of edit
actions taken in the system.

There is a technical roadmap entry planned for recording edit actions, but the 
edit history is not currently planned to be made publicly available via API.
Initially, it will only exist for the purposes of forensic analysis, when 
manually requested.

We may including the history of edit actions in the data export archives 
that are planned to made available for all public raid data.

## Identification of editing user

Note that all API integration is done at the api-token level and thus 
individual users of the client systems can not be identified in the audit logs.

At best, for all mint/edit actions carried out via API (which is intended to be 
most of them) - the "user" carrying out the edit action would be 
identified as "a RedBox user @ UNDA".

## Auditing of data access

Raido does no auditing of view actions and there is no plan to implement 
any - this includes "closed" and "embargoes" data.




