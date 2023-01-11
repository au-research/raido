package raido

import static raido.OsUtil.osCmd

class GitUtil {
  static long describeTimeout = 5000

  static String describe(String tagPrefix, Object overrideEnvName){
    
    String environmentVersion = System.getenv(overrideEnvName.toString())
    
    if( environmentVersion ){
      return environmentVersion
    }
   
    /* https://git-scm.com/docs/git-describe
    • The `describe` functionality works off of the metadata in the git repo. 
      If you're working with a "shallow clone", it's possible none of the 
      commits you've cloned are tagged appropriately, in which case the
      `--always` flag ensures that the result will not fail, it will just 
      report the commit hash as the description, i.e. `bca0f3e5` or similar.
    • If there are uncommitted changes on your local workspace, describe will 
      append `.dirty` to the reported result, i.e. `sto-raido-v1.0.dirty1` or 
      `bca0f3e5.dirty` or similar.
    • `--match` param is a glob pattern, not a regex
    */
    var args = osCmd(
      "git", "describe", "--always", "--dirty", "--match", "$tagPrefix*"
    )

    var stdOut = new StringBuilder()
    var stdErr = new StringBuilder()
    
    var proc = args.join(" ").execute()
    proc.consumeProcessOutput(stdOut, stdErr)
    proc.waitForOrKill(describeTimeout)
    assert !stdErr
    
    return stdOut?.toString()?.trim()
  }
  
  static String fullCommitHash(Object overrideEnvName){
    String environmentVersion = System.getenv(overrideEnvName.toString())

    if( environmentVersion ){
      return environmentVersion
    }

    /* rev-parse is technically "plumbing" but it was the easiest way I could
       find to get the hash, without needing to parse the result. */
    var args = osCmd("git", "rev-parse", "HEAD")

    var stdOut = new StringBuilder()
    var stdErr = new StringBuilder()

    var proc = args.join(" ").execute()
    proc.consumeProcessOutput(stdOut, stdErr)
    proc.waitForOrKill(describeTimeout)
    assert !stdErr

    return stdOut?.toString()?.trim()
  }
}
