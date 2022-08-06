# raido-v2

It's called "v2" because initially it was called raid-v2 because there was a
v1. Should've just called it "raido" - oh well.

Important architecural decisions are recorded in [/doc/adr](./doc/adr).

Different sub-projects have their own ADR log, in their local /doc/adr
directories (i.e. db, app, [api](/api-svc/spring/doc/adr), etc.)

# CI

Using a Github action at the moment to run unit tests.

* [CI - gradle.yml](.github/workflows/gradle.yml)
  * builds and runs unit tests and integration tests
  * executes on pusto to `main` or on pull-requests
    * So if you want to test stuff in Github without pushing to `main`;
      work in your own branch, create a PR and it will run the build
      and tests.
  * Changes to `*.md` files are not built.

* Look in [Github Actions](https://github.com/au-research/raido-v2/actions)
  console to see what's going on


# Development pre-requisites

Generally:

* [JDK 17](./doc/adr/2022-07-21_jdk-platform.md)
* [Node.js 16](./doc/adr/2022-07-21_nodejs-platform.md)
* Docker
  * postgres 14 
  

