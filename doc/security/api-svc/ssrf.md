https://owasp.org/www-community/attacks/Server_Side_Request_Forgery

SSRF is currently of particular concern for us because we validate 
various user-provided PID values using simple HTTP calls.


## Short term

Each call to an external service is isolated to it's own internal component and
we do regex based validation before making any HTTP Request to validate the 
PID value.


## Long term

This issue will be mitigated over time as we expect each of the PID integrations
that we do to be expanded into mature API-level integrations using fully-formed 
API calls, API calls will be properly sanitised and validated on 
the remote side.  