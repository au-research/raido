package raido.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.util.DateUtil.UTC_ZONE_ID;
import static raido.util.DateUtil.ISO_MINUTES_FORMAT;
import static raido.util.DateUtil.formatIsoDateTime;
import static raido.util.DateUtil.formatUtcDateTime;
import static raido.util.DateUtil.parseDateTime;
import static raido.util.Log.to;

public class DateUtilTest {
  private static final Log log = to(DateUtilTest.class);


  /** This test should pass regardless of JVM user.timezone setting */
  @Test
  public void testFormatParseSymmetry() {
    log.info("TZ: " + ZoneId.systemDefault().toString());

    var expectedDateTime =
      LocalDateTime.of(2000, 1, 2, 3, 4);
    String expectFormattedValue = "2000-01-02 03:04";

    assertThat(formatUtcDateTime(ISO_MINUTES_FORMAT, expectedDateTime)).
      isEqualTo(expectFormattedValue);

    assertThat(
      parseDateTime(ISO_MINUTES_FORMAT, UTC_ZONE_ID, expectFormattedValue)
    ).isEqualTo(expectedDateTime);
  }

  @Test
  public void testFormatIsoInstant() {
    var expectedDateTime =
      LocalDateTime.of(2000, 1, 2, 3, 4);
    assertThat(formatIsoDateTime(expectedDateTime)).
      isEqualTo("2000-01-02T03:04:00");
    
  }
}