
# Build pre-requisites

* Node.js 16
* Java 17
  * so that the generated code can be built from the IDL via openapi-generator 
  * see [build-troubleshooting.md](../doc/build-troubleshooting.md)
  for any errors (e.g. "java not found")
  * see [openapi-typescript.md](./doc/openapi-typescript.md) for some notes
  about this tech and possible alternatives


# Gradle build

The gradle build in this project is standalone, not a part of the overall
raido-v2 gradle build.
* because the `git-version` plugin was dying when run inside the codepipeline
* because the source artifact just brings the code, not the `.git` dir
* improvements to sort this out
  * I want to change the codepipeline to use the newer codestar connection stuff
  * And get rid of the git-version plugin, requiring Git to be present isn't very
    "pipeline" friendly.
  * Consider converting the build to a simple "codebuild", rather than a 
    "codepipeline"

  
# Developing 

Look in [build-troubleshooting](../doc/build-troubleshooting.md) if having
Node/Java problems.

## Running a local Node.js server in IDEA

* Right-click the `/app-client/package.json` and do `Run 'npm install'` to get
libraries installed
* Right click `package.json` and do `Show npm scripts`
  * this adds the project to the IDEA `npm tool window`, which is similar to 
  the gradle window.  You can launch scripts declared by `package.json` by 
  double-clicking them, etc.
* run the `start` script
  * this will first run the `prestart` script which will generate code 
    that is needed to compile (OpenAPI code from the IDL)
  * then it will launch the app, listening on url `http://localhost:7080` 
  * the `proxy` setting in `package.json` is set to forward all requests to 
  `http://localhost:8080`, which is where you're expected to be running the 
  `api-svc` during local dev
    * you could change this to `https://demo.raido-infra.com` if you don't want
    to run the api-svc locally
      * don't commit that, preferably find a way to customise it in a local 
      file or something (and add it to this doco)

## Running a local Node.js server from command line

* in the directory: `<repo>/app-client`
* run `npm install`
  * this step installs all the npm dependencies locally
* run `npm run start`
  * this step runs a local Node.js web server
  * the node.js server takes care of building and serving the app so you can
  access it in your local browser at `http://localhost:7080`


## Debugging the app in IDEA

When you want to debug client code, you don't "debug" the `start` task.  You
leave it running as normal and run a new browser that is linked to IDEA. 

https://www.jetbrains.com/help/idea/react.html#react_running_and_debugging_debug

It wasn't auto-created on my machine as the doco says.
Just add a new run config of type `Javascript Debug` and set the URL to 
`http://localhost:7080`.
When you "debug" that config, it will launch a new browser and any debug 
breakpoints that are set in IDEA will stop execution and you can dbug in IDEA,
just like a Java app.


## Debugging React components

Don't forget to install the React developer extension into your browser.
You can easily browse around the component hierarchy and see state easily, 
instead of adding console debug statements.

You can also use it to profile the UI code and look for poor performing code 
and superfluous re-renders.


## Deploying to `demo` environment

Log in to AWS and go the CodePipeline console.
Look for the pipeline named `AppClient` and click the "release changes" button.

https://ap-southeast-2.console.aws.amazon.com/codesuite/codepipeline/pipelines/AppClient/view?region=ap-southeast-2#


# Build origination

This project was bootstrapped with:
```
cd .../raido-v2
npx create-react-app app-client --template typescript
```

* renamed some strings in `/public` from defaults to "Raido".
* added the icons, see [doc/icon.md](doc/icon.md)
* replaced the react `src/log.svg` with raido
* updated `App.tsx` to show Raido stuff


# NPM notes on dependencies

This is in the readme because you can't add comments to `package.json`.

* `"json5": ">=2.2.2"` - added 2023-01-10 to resolve a dependabot alert
  * currently an unused transitive dep, should be able to remove it after all 
    libs are updated to force it themselves
* `cross-env` - Allows you to set ENV variables in npm scripts in a way that
  works on both Windows and Linux, https://www.npmjs.com/package/cross-env.
  I can't remember what I was doing that needed this, can probably be removed.
