package raido.apisvc.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.util.DateUtil.ISO_MINUTES_FORMAT;
import static raido.apisvc.util.DateUtil.UTC_ZONE_ID;
import static raido.apisvc.util.DateUtil.formatDynamoDateTime;
import static raido.apisvc.util.DateUtil.formatIsoDateTime;
import static raido.apisvc.util.DateUtil.formatUtcDateTime;
import static raido.apisvc.util.DateUtil.parseDateTime;
import static raido.apisvc.util.Log.to;

/*
 IMPROVE: save and restore the current system default timezone, then
 write tests that prove correct behaviour.
*/
public class DateUtilTest {
  private static final Log log = to(DateUtilTest.class);
  
  // applicable UTC offset at this instant, for sydney, is +11 
  public static final LocalDateTime DAYLIGHT_SAVINGS_EXAMPLE = 
    LocalDateTime.of(2000, 1, 2, 3, 4);


  /** This test should pass regardless of JVM user.timezone setting */
  @Test
  public void testFormatParseSymmetry() {
    log.info("TZ: " + ZoneId.systemDefault());

    var expectedDateTime = DAYLIGHT_SAVINGS_EXAMPLE;
    String expectFormattedValue = "2000-01-02 03:04";

    assertThat(
      parseDateTime(ISO_MINUTES_FORMAT, UTC_ZONE_ID, expectFormattedValue)
    ).isEqualTo(expectedDateTime);
    
    assertThat(formatUtcDateTime(ISO_MINUTES_FORMAT, expectedDateTime)).
      isEqualTo(expectFormattedValue);

  }

  @Test
  public void testFormatIsoDateTime() {
    var expectedDateTime = DAYLIGHT_SAVINGS_EXAMPLE;
    assertThat(formatIsoDateTime(expectedDateTime)).
      isEqualTo("2000-01-02T03:04:00");
  }
  
  @Test
  public void testFormatDynamoDateTime() {
    log.info("TZ: " + ZoneId.systemDefault());

    assertThat(formatDynamoDateTime(DAYLIGHT_SAVINGS_EXAMPLE)).
      isEqualTo("2000-01-02 14:04:00");
  }
}