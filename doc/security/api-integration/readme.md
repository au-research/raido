See the [access-control](../access-control) directory for information about 
the `api-key` and `api-token` concepts.

Client implementations should pay specific attention to the 
[xss.md](../api-svc/xss.md) topic, specifically: all clients implementations 
must perform output-encoding on all RAiD metadata fields.  
