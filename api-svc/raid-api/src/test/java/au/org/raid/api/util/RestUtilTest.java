package au.org.raid.api.util;

import org.junit.jupiter.api.Test;

import static au.org.raid.api.util.RestUtil.sanitiseLocationUrl;
import static org.assertj.core.api.Assertions.assertThat;

public class RestUtilTest {
    @Test
    public void testSanitise() {
        assertThat(
                sanitiseLocationUrl("https://demo.raido-infra.com#id_token=xxx.yyy.zzz")
        ).isEqualTo("https://demo.raido-infra.com");

        assertThat(
                sanitiseLocationUrl(
                        "https://demo.raido-infra.com#id_token=xxx.yyy.zzz&somethingElse=blah")
        ).isEqualTo("https://demo.raido-infra.com");

        assertThat(
                sanitiseLocationUrl("https://demo.raido-infra.com?param=value" +
                        "#id_token=xxx.yyy.zzz&somethingElse=blah")
        ).isEqualTo("https://demo.raido-infra.com");

        assertThat(
                sanitiseLocationUrl("https://demo.raido-infra.com?param=value")
        ).isEqualTo("https://demo.raido-infra.com");

        assertThat(
                sanitiseLocationUrl("https://demo.raido-infra.com")
        ).isEqualTo("https://demo.raido-infra.com");

        assertThat(
                sanitiseLocationUrl("https://demo.raido-infra.com/")
        ).isEqualTo("https://demo.raido-infra.com/");

        assertThat(
                sanitiseLocationUrl("https://demo.raido-infra.com/path")
        ).isEqualTo("https://demo.raido-infra.com/path");

        assertThat(
                sanitiseLocationUrl(
                        "https://demo.raido-infra.com/path#id_token=xxx.yyy.zzz")
        ).isEqualTo("https://demo.raido-infra.com/path");
    }

    @Test
    public void testSanitiseEdgeCases() {
        assertThat(
                sanitiseLocationUrl(null)
        ).isEqualTo(null);

        assertThat(
                sanitiseLocationUrl("")
        ).isEqualTo("");

        assertThat(
                sanitiseLocationUrl(" ")
        ).isEqualTo(" ");

        assertThat(
                sanitiseLocationUrl("xxx")
        ).isEqualTo("xxx");

        assertThat(
                sanitiseLocationUrl("xxx?")
        ).isEqualTo("xxx");

        assertThat(
                sanitiseLocationUrl("xxx#")
        ).isEqualTo("xxx");

        assertThat(
                sanitiseLocationUrl("?")
        ).isEqualTo("");

        assertThat(
                sanitiseLocationUrl("#")
        ).isEqualTo("");

        assertThat(
                sanitiseLocationUrl("?#")
        ).isEqualTo("");

        assertThat(
                sanitiseLocationUrl("https://?#")
        ).isEqualTo("https://");

        assertThat(
                sanitiseLocationUrl("//#")
        ).isEqualTo("//");
    }
}