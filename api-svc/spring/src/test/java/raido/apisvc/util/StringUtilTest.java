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
import static raido.apisvc.util.StringUtil.mask;

public class StringUtilTest {
  private static final Log log = to(StringUtilTest.class);

  /*
   Not too much thought went into the exat behaviour of mask() under all 
   possible token lengths - change the behaviour it if you want.
   Especially don't like the output when the value is only a little 
   longer than the mask length, for example a length 10 string shows
   7 of the chars and only redacts the last three.
   That's a bit dodgy, but short values like that aren't terribly secure in 
   the first place.
   Just make sure there's lots of edge testing, don't wnat prod falling over
   because the mask function failed on values of a certain length - that's 
   a nightmare intermittent scenario.
   */
  @Test
  public void testMaskRedaction() {

    assertThat(mask(null)).isEqualTo("");
    assertThat(mask("")).isEqualTo("");
    assertThat(mask("x")).isEqualTo("...1");
    assertThat(mask("xx")).isEqualTo("...2");
    assertThat(mask("xxx")).isEqualTo("...3");
    assertThat(mask("xxxx")).isEqualTo("...4");
    assertThat(mask("xxxxxxxxx")).isEqualTo("...9");
    assertThat(mask("xxxxxxxxxx")).isEqualTo("xxxxxxx...10");
    assertThat(mask("xxxxxxxxxxx")).isEqualTo("xxxxxxx...11");
    assertThat(mask("0123456789", 20)).isEqualTo("...10");
  }

  /*
  Don't care what the output is, just care that it doesn't fail.
   */
  @Test
  public void testMaskEdgeCases() {
    assertThat(mask(null, -1)).isEqualTo("");
    assertThat(mask("x", -1)).isEqualTo("...1");
    assertThat(mask("x", 0)).isEqualTo("...1");
    assertThat(mask(null, 0)).isEqualTo("");
    assertThat(mask("", 0)).isEqualTo("");
    assertThat(mask("x", 1)).isEqualTo("...1");
    assertThat(mask("xxxx", 1)).isEqualTo("...4");
    assertThat(mask("xxxxxxxxx", 0)).isEqualTo("...9");
    assertThat(mask("xxxxxxxxxx", 0)).isEqualTo("...10");
    assertThat(mask("xxxxxxxxxxx", 0)).isEqualTo("...11");

  }


}