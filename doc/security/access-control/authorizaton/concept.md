
## `service-point`

The basic administrative unit, in Raido.

An institution might have many service-points that have separate agreements  
with the registration-agency.  

For example, a large University  might have separate service-points, one for
their library department and one for their IT department.

Every service-point is associated with an institution via a 
[RoR](https://ror.org/) which Raido stores as the identifier-owner for that
service-point.

Every raid that is minted in Raido is associated with a service-point that is 
responsible for that data.

The service-point also defines who can edit (or see closed data of) a raid.

Theoretically, service-point is not a "RAiD" concept, the idea was created 
for Raido, in order to have a base entity to associate with user roles and 
raid data.

That said, every RAiD is minted in the context of a service-point (and 
implicitly, an institution) and the responsible institution/service-point is 
published in the Raido metadata in the `identifierServicePoint` and 
`identifierOwner` fields of the 
[`IdBlock`](/api-svc/idl-raid-v2/src/metadata-block.yaml).

The fields are published and documented in the "Raido" OpenAPI spec, but are 
not currently part of the formal RAiD metadata schema specification. 


## `api-key`

See [api-key.md](./api-key.md)


## `api-token`

See [api-key.md](./api-key.md)


## `app-user`

See [app-user](./app-user.md)

## Role 

Every api-key and app-user has a role associate with it, see 
[role.md](./role.md).