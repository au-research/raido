package au.org.raid.api.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum SchemaValues {
    DOI_SCHEMA_URI("https://doi.org/"),
    ROR_SCHEMA_URI("https://ror.org/"),
    ORCID_SCHEMA_URI("https://orcid.org/"),
    GEONAMES_SCHEMA_URI("https://www.geonames.org/"),
    SIL_SCHEMA_URI("https://www.iso.org/standard/39534.html"),
    SUBJECT_SCHEMA_URI("https://linked.data.gov.au/def/anzsrc-for/2020/"),
    ACCESS_TYPE_OPEN("https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json"),
    ACCESS_TYPE_CLOSED("https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json"),
    ACCESS_TYPE_EMBARGOED("https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/embargoed.json"),
    PRIMARY_DESCRIPTION("https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json");

    private final String uri;
}
