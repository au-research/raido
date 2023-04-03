
## API key 

Referred to in most documentation as "api-key", referred to in the 
app-client as an "API key".

The api-key is the entity that an OPERATOR or SP_ADMIN user creates to allow
integration via the API.

## API token

Referred to in most doco as "api-token", referred to in the app-client as just
"token".

The api-token is the actual JWT object that is sent with every API request as a
bearer token in the `Authorization` header for each HTTP request.

Many api-tokens can be associated with a single api-key. 

The api-token is not stored in the RAiD service, once it is generated it is
thrown away.


