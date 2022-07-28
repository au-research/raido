package raido.util.test;

import raido.util.Log;

import static raido.util.Log.to;

/**
 The method names are uppercase not because they're static, but to make them
 stand out in the test source.
 
 Same with uppercasing the actual message.
 
 I like to use the `Grep Console` IDEA plugin to highlight matching `BddUtil.*`
 to make these lines stand out in the console log.  Makes interpreting test 
 output very easy.
 
 This exists in prod source because I can't figure out how to get Gradle to 
 depend on unit test classes from functional tests.
 */
public class BddUtil {
  private static final Log log = to(BddUtil.class);
  
  public static void log(String classifier, String descrtiption) {
    log.info(classifier.toUpperCase() + ": " + descrtiption);
  }

  public static void GIVEN(String description) {
    log("given", description);
  }

  // use expect for when you so when/then in a single statement (think AssertJ)
  public static void EXPECT(String description) {
    log("expect", description);
  }

  public static void WHEN(String description) {
    log("when", description);
  }

  public static void THEN(String description) {
    log("then", description);
  }

  public static void AND(String description) {
    log("and", description);
  }
}
