package au.org.raid.api.test.util;

import au.org.raid.api.util.Log;

/** Does nothing but log statements in a specific format. I set my
 console view to highlight that format so it stands out. 

 I like to use the `Grep Console` IDEA plugin to highlight matching `BddUtil.*`
 to make these lines stand out in the console log.  Makes interpreting test 
 output very easy.
 
 I find myself often writing log statements in integration/functional tests
 to delineate logical sections of test code.  It helps when inspecting the 
 log output for a failed test and often helps track down other issues too -
 e.g turn on SQL/transaction tracing and you can track the SQL issued to what 
 the test is doing, often turns up surprising things.
 
 The method names are uppercase not because they're static, but to make them
 stand out in the test source.
 
 Same with upper-casing the actual message.
 */
public class BddUtil {
  private static final Log log = Log.to(BddUtil.class);
  
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
