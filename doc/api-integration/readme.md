See the [access-control](../security/access-control) directory for information 
about the `api-key` and `api-token` concepts.

Client implementations should pay specific attention to the 
[xss.md](../security/api-svc/xss.md) topic, specifically: all clients 
implementations 
must perform output-encoding on all RAiD metadata fields.  
