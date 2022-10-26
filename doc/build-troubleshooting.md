
# Java or node.exe / NPM not found

I normally install Java and Node using the `.zip` portable archives, meaning
neither tool is on my global path nore are global environment variable set
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


# Typescript compile taking a long time

On a modern machine, it should only take about 5-10 seconds for running 
`npm run start` to do the full TypeScript compile and start responding to 
request (after that, an incremental compile should only take a second or two).

If you feel like it's really slow, consider if your machine has "real time" 
scanning enabled in its security configuration.  That means the machine is 
doing virus/malware scanning on every file that is being loaded for the compile.
That's likely going to slow the compile down significantly.
Consider if this benefit is worth the protection this scanning is giving you
(likely minimal, think about it).

To diagnose, try *temporarily* disabling the real-time scanning to see if 
it speeds up the compile. Remember to consider caching, with scanning
enabled, do the compile twice - the first time to get everything cached, the 
second time to measure how long it takes.

Then, *temporarily* disable the real-time scanning and do the compile again.
Now re-enable, the real-time scanning.

If the compile is appreciably faster, consider whether you want do disable 
real-time scanning for your compile.

If you decide to do this, you want leave real-time scanning enabled, but add
exclusions so that the software knows not to scan your build stuff.

Consider excluding:
* Node.Js and Java installed directories
* The directory with the source code (especially because it contains the 
  `node_modules` directory).
* The cache directories for Maven/Gradle
* The directory you load your IDE from (and it's configuration/caching)
  * e.g. IDEA creates large GB sized cache files, scanning those is pointless.
  * Do not add your home directory as a real-time scanning exclusion.

Don't add your entire home directory as an exclusion.

If your tooling stores stuff in your home directory and you can't/don't want
to change that.  Add specific exclusions for things inside your home directory.

Also consider if your IDE is writing configuration/caching info to your
home directory - you probably don't want to do that, set it up to use a local
directory somewhere else on your disk that is included in your exclude list.

This means any "roaming profile" won't be saving/reading your gigabytes of 
cache files to the network server.  Decide for yourself if that's a good thing
or a bad thing.


