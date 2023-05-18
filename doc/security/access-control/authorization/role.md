There are currently three roles implemented in the system.


# `OPERATOR` - system-operator

Only assigned to folks who maintain and support the system.

Allows operators to perform actions regardless of their service-point (which
is usually "raido") - for example, an operator can edit a raid associated
with any service-point.

There is not currently any usage of the `OPERATOR` role in a machine
access context.


# `SP_ADMIN` - service-point-administrator

An SP_ADMIN can:

* approve/deny authorization requests for that service-point
* maintain the service point (update name, contact fields, etc.)
* maintain app-users (disable them, change their role)
* maintain api-keys and generate api-tokens for use in API integration
* perform the same raid-related actions for the service-point as an SP_USER


# `SP_USER` - service-point-user

An SP_USER can:

* list recently minted raids 
  * including export the listed data as a CSV file
* mint new raids
* maintain raid data