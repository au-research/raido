package raido.apisvc.util;

import java.time.LocalDate;

public class TestConstants {
  public static final String TITLE_TYPE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1";

  public static final String TITLE = "Test Title";

  public static final String PRIMARY_TITLE_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json";

  public static final String ALTERNATIVE_TITLE_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/alternative.json";

  public static final String UNKNOWN_TITLE_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/unknown.json";

  public static final LocalDate START_DATE = LocalDate.now().minusMonths(1);
  public static final LocalDate END_DATE = LocalDate.now();
}
