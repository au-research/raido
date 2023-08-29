package au.org.raid.api.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum SchemaUri {
    DOI("https://doi.org/"),
    ROR("https://ror.org/"),
    ORCID("https://orcid.org/"),
    GEONAMES("https://geonames.org/"),
    SIL("https://www.iso.org/standard/39534.html")
    ;

    private final String uri;
}
