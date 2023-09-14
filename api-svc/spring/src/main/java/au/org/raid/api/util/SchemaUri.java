package au.org.raid.api.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum SchemaUri {
    DOI("https://doi.org/"),
    ROR("https://ror.org/"),
    ORCID("https://orcid.org/"),
    GEONAMES("https://www.geonames.org/"),
    SIL("https://www.iso.org/standard/39534.html"),
    SUBJECT("https://linked.data.gov.au/def/anzsrc-for/2020/")
    ;

    private final String uri;
}
