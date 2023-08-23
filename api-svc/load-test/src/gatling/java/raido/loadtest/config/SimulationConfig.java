package raido.loadtest.config;

import au.org.raid.api.util.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.StringJoiner;

import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.hasValue;
import static java.lang.Integer.parseInt;
import static java.time.Duration.ofSeconds;
import static raido.loadtest.util.Jvm.cwd;

public class SimulationConfig {
  private static final Log log = to(SimulationConfig.class);
  
  public static final SimulationConfig simConfig = new SimulationConfig();
  
  public static final String SUBPROJECT_DIR = "api-svc/load-test";
  public static final String DATA_DIR = "build/feeder-data";

  public int rampUpSeconds;
  public int steadyStateSeconds;
  public int userCount;
  
  /** set to 0 for "Hulk, smash" 😠🟢💪 */
  public long thinkTimeMultiplier;

  public SimulationConfig() {
    rampUpSeconds = parseInt(getConfig("rampUpSeconds", "10"));
    steadyStateSeconds = parseInt(getConfig("steadyStateSeconds", "20"));
    userCount = parseInt(getConfig("users", "1"));
    thinkTimeMultiplier = parseInt(getConfig("thinkTimeMultiplier", "1"));
    
    // do not commit
//    thinkTimeMultiplier = 1;
//    userCount = 50;
//    steadyStateSeconds = 60;

    var testDataFile = getDataPath("touch.txt");
    File dataDir = testDataFile.getParent().toFile();
    if( !dataDir.exists() ){
      log.with("dataDir", dataDir).
        with("mkdirs()", dataDir.mkdirs()).
        info("creating data dir for feeders");
    }
    
    log.with("config", this.toString()).
      with("cwd", cwd().toAbsolutePath()).
      with("testDataFile", testDataFile.toString()).
      info("startup");
  }

  public static String getConfig(String name, String defaultValue) {
    String configName = "%s.%s".formatted(
      SimulationConfig.class.getSimpleName(), name);

    String envValue = System.getenv(configName);
    if( hasValue(envValue) ){
      return envValue;
    }

    return System.getProperty(configName, defaultValue);
  }

  @Override
  public String toString() {
    return new StringJoiner(
      ", ",
      SimulationConfig.class.getSimpleName() + "[",
      "]")
      .add("rampUpSeconds=" + rampUpSeconds)
      .add("steadyStateSeconds=" + steadyStateSeconds)
      .add("userCount=" + userCount)
      .add("thinkTimeMultiplier=" + thinkTimeMultiplier)
      .toString();
  }

  /* When running Gradle via IDEA, CWD will be: `<repo>/api-svc/load-test` 
  If running gradlew in the normal way (via invoking `gradlew` with the repo 
  root as the CWD) it will be: `<repo>/`
  Seems IDEA sets the CWD to be the sub-project's dir, while if we run the 
  task from the repo root in the normal way, the CWD will be the repo root.
  The Gatling Gradle task appears to have no way to set the CWD (you could 
  maybe set `user.dir` SysProp, but that's never a good idea). 
  Note: it *is* possible to cd into the sub-project and run gradlew via 
  `../../gradlew` but I don't want to have to remember to do that just for 
  Gatling tasks.
  */
  public Path getDataPath(String fileName){
    Path subProjectPath = Paths.get(SUBPROJECT_DIR);
    boolean isExecutedInRootDir = !cwd().endsWith(subProjectPath);
    
    String filePath; 
    if( isExecutedInRootDir ){
      filePath = SUBPROJECT_DIR + "/" + DATA_DIR + "/" +fileName;
    }
    else {
      filePath = DATA_DIR + "/" +fileName;
    }

    log.with("cwd", cwd()).
      with("fileName", fileName).
      with("filePath", filePath).
      with("subProjectPath", subProjectPath).
      with("isExecutedInRootDir", isExecutedInRootDir).
      debug("dataPath");

    return Paths.get(filePath).toAbsolutePath();
  }

  public Duration thinkForSeconds(int seconds){
    return ofSeconds(seconds * thinkTimeMultiplier);
  }
}
