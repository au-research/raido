
# Build pre-requisites

* Node.js 16
* Java 17
  * so that the generated code can be built from the IDL via openapi-generator 
  * see [build-troubleshooting.md](/doc/build-troubleshooting.md)
  for any errors (e.g. "java not found")
  * see [openapi-typescript.md](./doc/openapi-typescript.md) for some notes
  about this tech and possible alternatives


# Gradle build

The gradle build in this project is standalone, not a part of the overall
raido-v2 gradle build.
* because the `git-version` plugin was dying when run inside the codepipeline
* because the source artifact just brings the code, not the `.git` dir
* I want to change the codepipeline to use the newer codestar connection stuff
  * or get rid of the git-version plugin


# Build origination 

This project was bootstrapped with:
```
cd .../raido-v1
npx create-react-app app-client --template typescript
```

* renamed some strings in `/public` from defaults to "Raido".
* added the icons, see [doc/icon.md](doc/icon.md)
* replaced the react `src/log.svg` with raido
* updated `App.tsx` to show Raido stuff


