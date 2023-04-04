
# "App user" and "Authorization requests"

Referred to in doco as "app-user", referred to in the app-client as just "user".

When a user authenticates via OIDC, they are not yet an app-user.

First, they're directed to the "authorization request" page, where they submit
a request to be authorizes "for" a specific service-point.  Then a user with 
sufficient privilege (`SP_ADMIN` or `OPERATOR` role) can "approve" their 
request for that specific service-point.

Upon approval, a record is inserted into the `app_user` table that authorizes
the user to use the app-client.

app-users can be created with either `SP_USER` or `SP_ADMIN` role.

If a user needs to change their service point (because of error or because 
that's what happened in the real world) - the app-user record  can be "disabled"
at which point the user can re-submit an "authorization request" for the new
service-point.


# App user API tokens

Referred to in most doco as "api-token", not generally used in the UI.

Though there's an OPERATOR-only feature that refers to setting a token cutoff, 
used to blacklist all api-tokens issued for an api-user before that point.

The api-token is the actual JWT that is sent with every API request as a
bearer token in the `Authorization` header for each HTTP request.

When the app-user is authenticated via OIDC, and after we've verified that the
identity is for a an "authorized user", they are issued an api-token (sometimes
called an "authorization token" in the code).  The api-tokens tokens issued 
to app-users are ephemeral (current default expiry: 9 hours).


### Future changes to api-token format

We ought to rename the `EMAIL` claim to `IDENTITY`.

When this is done though, we will keep the authorization logic backward
compatible so that all api-tokens issued with the previous claims will
continue to work.  The api-tokens for app-users are ephemeral (currently 
default is 9 hours), so we won't have to carry the backwards compatibility 
logic for long.


# Database model

At the moment, both app-user and api-key are stored in the `app_user` table.

Note that the `email` column ought to be renamed to `identity` - "email"
doesn't even work for ORCID sign-ins (users can make all their email
addresses private in ORCID), let alone api-tokens. 

