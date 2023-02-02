
# Java or node.exe / NPM not found

I normally install Java and Node using the `.zip` portable archives, meaning
neither tool is on my global path nor are global environment variables set
such as `JAVA_HOME`, `NODE_HOME` or `NPM_HOME`.

Using IDEA, I manually configure the node/npm and JDK in IDEA settings.
Sometimes, where we call Gradle from NPM or vice-versa, you may need to set
the appropriate `JAVA_HOME` / `NODE_HOME` env variable in your run configuration
(remember you can specify these in the defaults for all run configurations).

If you follow the standard wizard-based install process for Java/Node, during
installation the executables and env vars will be added on your global
environment and you shouldn't see this issue.

The only real downside to doing the standard install in a "single-project" 
environment is that diagnosing version issues, testing new versions, 
upgrading/downgrading when the project prerequisites change etc. can be a pain.

Some pople use https://sdkman.io/ for managing their environment, opther people
use the package manager of their local system.


# Typescript compile taking a long time

On a modern machine, it should only take about 5-10 seconds for running 
`npm run start` to do the full TypeScript compile and start responding to 
requests (after that, an incremental compile should only take a second or two).

If you feel like it's really slow, consider if your machine has "real time" 
scanning enabled in its security configuration.  That means the machine is 
doing virus/malware scanning on every file that is being loaded for the compile.
That's likely going to slow the compile down significantly.
Consider if the benefit is worth the protection this scanning is giving you
(likely minimal, think about it).

To diagnose, try *temporarily* disabling the real-time scanning to see if 
it speeds up the compile. Remember to consider caching, with scanning
enabled, do the compile twice - the first time to get everything cached, the 
second time to measure how long it takes.

Then, *temporarily* disable the real-time scanning and do the compile again.
(don't forget to re-enable real-time scanning after your finished testing).

If the compile is appreciably faster, consider whether you want do disable 
real-time scanning for your compile.

If you need to do something about real-time scanning; you should leave 
real-time scanning enabled, but add
exclusions so that the software knows not to scan your development artifacts.

Consider excluding:
* Node.Js and Java installed directories
* The directory with the source code (especially because it contains the 
  `node_modules` directory).
* The cache directories for Maven/Gradle
* The directory you load your IDE from (and its configuration/caching dirs)
  * e.g. IDEA creates large GB sized cache files, scanning those is pointless

Don't add your entire home directory as an exclusion.
Usually, your home directory will be configured as the target for
downloaded files (from your browser, mail-client etc.)  
If you think real-time scanning provides benefits - these are definitely
files you want being scanned.

If your tooling (IDEA, Maven, Gradle, etc.) stores stuff in your home directory 
and you can't/don't want to change that - add specific exclusions for things 
inside your home directory.

Also consider if your IDE is writing configuration/caching info to your
home directory - you probably don't want to do that, set it up to use a local
directory somewhere else on your disk that is included in your exclude list.

This means any "roaming profile" won't be saving/reading your gigabytes of 
cache files to the network server.  Decide for yourself if that's a good thing
or a bad thing.


# CodeBuild project fails because "too many requests" pulling from DockerHub

Known issue because we share source IP addresses with all the other CodeBuild
projects out there.  

Workaround is to just try again.
Potential fixes listed in [technical-debt.md](./technical-debt.md).


# Gradle "Could not resolve plugin" on GitHub CI environment

The cause was that the `gradle-jooq-plugin` seems to require JDK 17 and I forgot
to add JDK 17 to the `app-client` CI build spec.

The reason this didn't come up previously was that the GitHub CI environment
has JDK 11 installed by default, and when we were running the `openApiGenerate`
task in a standalone Gradle build, there was no jooq plugin.

The fix is to add the Corretto 17 `uses` setup block to the CI build spec. 

```
> Could not resolve all files for configuration ':api-svc:db:raido:classpath'.
   > Could not resolve nu.studer:gradle-jooq-plugin:8.1.
     Required by:
         project :api-svc:db:raido > nu.studer.jooq:nu.studer.jooq.gradle.plugin:8.1
      > No matching variant of nu.studer:gradle-jooq-plugin:8.1 was found. The consumer was configured to find a runtime of a library compatible with Java 11, packaged as a jar, and its dependencies declared externally, as well as attribute 'org.gradle.plugin.api-version' with value '7.6' but:
          - Variant 'apiElements' capability nu.studer:gradle-jooq-plugin:8.1 declares a library, packaged as a jar, and its dependencies declared externally:
              - Incompatible because this component declares an API of a component compatible with Java 17 and the consumer needed a runtime of a component compatible with Java 11
```