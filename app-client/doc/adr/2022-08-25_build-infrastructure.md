### Use create-react-app to build

* Status: finalised
* Who:  finalised by STO
* When: finalised on 2022-08-25
* Related: none


# Decision

Use create-react-app to build


# Context

## STO opinion

Obvious choices for building the project:
* webpack
* rollup 
* esbuild 

create-react-app lets us get up and running.  
Webpack is just awful to deal with and very slow to improve.
Rollup is easier to use, but that's likely to be just because it's new - webpack
was touted as the "easy to use" alternative at one point.

Webpack/rollup are "transitional" technologies - they're only popular because
there's no other reasonable choices.

Esbuild looks like a better solution to me, but not sure if it has legs.

I chose c-r-a in the short term, the decision can be reversed later.


# Consequences / Implications

* c-r-a tends to lag React and other ecosystem features
* support is spotty and "best effort"
* limited 
  * single entry point
  * no support for using shared code or new tooling (yarn, npm workspaces, 
    etc.) 

