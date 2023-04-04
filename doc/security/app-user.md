
# App user

Referred to in doco as "app-user", referred to in the app-client as just "user".

When a user authenticates via OIDC, they are not yet an app-user.

First, they're directed to the "authorization request" page, where they submit
a request to be authorizes "for" a specific service-point.  Then a user with 
sufficient privilege (`SP_ADMIN` or `OPERATOR` role) can "approve" their 
request for that specific service-point.

Open approval, a record is inserted into the `app_user` table that will allow
the user to use the app-client.

app-users can be created with either `SP_USER` or `SP_ADMIN` role.

If a user needs to change their service point (because of error or because 
that's what happened in the real world) - the app-user record  can be "disabled"
at which point the user can re-submit an "authorization request" for the new
service-point.


# Database model

At the moment, both app-user and api-key are stored in the `app_user` table.

Note that the `email` column ought to be renamed to `identity` - "email"
doesn't even work for ORCID sign-ins (users can make all their email
addresses private in ORCID), let alone api-tokens. 

