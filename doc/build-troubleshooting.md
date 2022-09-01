
# Java or node.exe / NPM not found

I normall instally Java and Node using the `.zip` portable archives, meaning
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
enviornment is that diagnosing version issues, testing new versions, 
upgrading/downgrading when the project prequisites change etc. can be a pain.




