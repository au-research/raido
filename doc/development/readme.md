# CI

Using Github actions at the moment to run build, test and quality tasks.

These generally execute on push to to `main` or on pull-requests.
So if you want to test stuff in Github without pushing to `main`;
work in your own branch, create a PR and it will run the build and tests.
Note that changes consisting solely of `*.md` files are intended to be ignored.

* [api-svc-ci.yml](.github/workflows/api-svc-ci.yml)
  * builds and runs unit and integration tests
* [app-client-ci.yml](.github/workflows/app-client-ci.yml)
  * does a production build of the app-client
* [codeql-analysis.yml](.github/workflows/codeql-analysis.yml)
  * runs an api-svc build in the context of Github
    [codeql](https://github.com/github/codeql-action)

* Look in [Github Actions](https://github.com/au-research/raido/actions)
  console to see what's going on


# Running the RAID service

Information about how the ARDC RAiD service is run and operated can be found in
[operational-environment.md](../architecture/environment/operational-environment.md).

If you're intending run your own RAiD service as a registration-agency, you 
can follow that documentation, in conjunction with adapting the AWS CDK code to
your own environment.

You can request access to the private repository that contains the CDK code by 
emailing the team at `contact@raid.org`.


# Development

Pre-requisites and instructions for
[local development](./local-development.md)


## Coding standards

See [/doc/code](../code/readme.md) - there may also be further sub-project
specific standards local to that project, look in the local `/doc` directory.


## Building

AWS build and deployment is automated via AWS CodeBuild projects - see
[deployment-environment.md](../architecture/environment/deployment-environment.md)

Use the codebuild projects to point you in the direction of the build tasks 
that you need.  Check for any `readme.me` files or `/doc` directories nearby 
the component of interest - they should contain further and more detailed 
instructions for building/running the software.

See [build-troubleshooting.md](./build-troubleshooting.md) if
having issues.


## Development and Release Branching

Currently daily development, CI and DEMO builds and releases are all
done using the `main` branch.

Currently PROD is deployed from the `v1.2` branch, this approach is likely to
continue with future versions.

Releases are tracked and built using `git describe` functionality working off
of annotated tags with the prefix `raido-v-`.

[release-process.md](./release-process.md)

