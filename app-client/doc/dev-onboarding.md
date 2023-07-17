This page is a list of the stuff we talked about in the React handover.

Use it as a guide for where to start and the major things to touch on when 
onboarding a new developer.


# create-react-app

The app-client is a mostly stand-alone NPM project built via create-react-app
(which itself is just a wrapper facade for webpack),
see [build-infrastructure.md](./adr/2022-08-25_build-infrastructure.md) for 
discussion of the choices relating to build-infrastructure and also a pointer
to a discussion of the future of c-r-a (no longer considered a good choice
for new React projects).

It'll be a few years yet before c-r-a goes EOL, plenty of time to re-visit
the choice later, hopefully when better choices are available.


# gradle and openapi

The app-client uses TypeScript code generated from the OpenAPI yaml files, we
use the Gradle plugin to leverage the Java openapi-generator project that
generates the TypeScript code, 
see [openapi-typescript.md](./openapi-typescript.md).


## React initialization on load

When the user points a browser at the app, they end up loading the 
[index.html](../public/index.html) file, which causes the global javascript 
to be invoked (c-r-a includes the javascript into the html page as part of 
the build process), which resides in [index.tsx](../src/index.tsx).  The global
javascript initialises react and replaces `<div id='roor'/>` element with the 
instantiated [App](../src/App.tsx) component.

We try to keep the `App` component clear of business or rendering logic.  
Structure the way it is, it acts as a "map" or "index" of the key pieces
of the front-end architecture.

Use the `App` component as a guide to understanding the major pieces of 
important UI infrastructure:
* error handling
* authentication
* API authorization
* location management and navigation
* list of top level page concepts 



