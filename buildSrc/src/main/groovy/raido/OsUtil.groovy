package raido

import org.apache.tools.ant.taskdefs.condition.Os

/** Create command that will run on either windows or linux, assuming the 
actual command has the same name and is on the path. */
class OsUtil {

  /**
   * Prefix the given command with `cmd -c` if on windows. 
   */
  static Iterable<String> osCmd(String... commands) {
    def newCommands = []
    if( Os.isFamily(Os.FAMILY_WINDOWS) ){
      newCommands = ['cmd', '/c']
    }

    newCommands.addAll(commands)
    return newCommands
  }
}
