
# Guidelines

> At least just think about it for a second.


## Configure for development by default

See [api-svc/.../config/environment/readme.md](/api-svc/spring/src/main/java/raido/apisvc/spring/config/environment/readme.md)


## Not-null by default

Use empty strings and arrays for defaults, etc.

But you can use null/undefined a bit more in Typescript code since it has 
decent support for truthiness concept and better handling of falsey values 
( `?.`  and `??`  etc.)


## Avoid usage of `any`

Favour static typing.


## Use absolute paths

The tsconfig is set up for absolte paths, you should use them - it just looks
nice.

Remember to configure your IDE for absolute paths.

In IDEA, tye option is uner "code styling", look for "user paths relative to 
tsconfig.json".


# Use Init Camel case for file names

e.g. `DateUtil.ts` instead of `date-util.ts`

Honestly, hyphenating the name is just bizarre.


# Use init cap for directory paths

This way you know when looking at an import that it's Raido specific.

e.g. if the import path starts with a capital letter, it's probably ours -  
`import {ErrorInfo} from "Error/ErrorUtil"`.


