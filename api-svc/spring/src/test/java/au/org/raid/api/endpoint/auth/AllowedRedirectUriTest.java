package au.org.raid.api.endpoint.auth;

import org.junit.jupiter.api.Test;

import static au.org.raid.api.endpoint.auth.AppUserAuthnEndpoint.isAllowedRedirectUri;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

public class AllowedRedirectUriTest {
  @Test
  public void testIsAllowedMatches() {

    assertThat(isAllowedRedirectUri(
      of("example1.com", "example2.com"), "example1.com"
    )).isTrue();
    assertThat(isAllowedRedirectUri(
      of("example1.com", "example2.com"), "example2.com"
    )).isTrue();
    
    // things that succeed, but should they?
    assertThat(isAllowedRedirectUri(of(""), "")).isTrue();
  }


  @Test
  public void testNotIsAllowedMatches() {

    assertThat(isAllowedRedirectUri(
      emptyList(), "example1.com"
    )).isFalse();
    assertThat(isAllowedRedirectUri(
      of("example1.com", "example2.com"), ""
    )).isFalse();
    assertThat(isAllowedRedirectUri(
      of("example1.com", "example2.com"), "example3.com"
    )).isFalse();
    assertThat(isAllowedRedirectUri(
      of("example1.com"), ""
    )).isFalse();
    assertThat(isAllowedRedirectUri(
      of("example1.com"), " "
    )).isFalse();
    assertThat(isAllowedRedirectUri(
      of("example1.com"), null
    )).isFalse();
  }


}