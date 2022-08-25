### Use local state and React context for state management

* Status: finalised
* Who:  finalised by STO
* When: finalised on 2022-08-25
* Related: none


# Decision

Use local state and React context for state management.


# Context

## STO opinion

Obvious choices for state management libraries are:
* Redux
* MobX, etc.

The popular state management libraries have poor support for Typescript and
static typing.  There are workarounds and ways to manage that, but Raido 
just doesn't need a lot of global state anyway.

The react-query library will handle server related state management, caching, 
etc. You could even make the argument that react-query is our state management 
library.


# Consequences / Implications

* Need to understand React context and use local state for most things.

