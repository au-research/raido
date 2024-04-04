package au.org.raid.api.vocabularies.raid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelatedRaidType {
    OBSOLETES("https://vocabulary.raid.org/relatedRaid.type.schema/198"),
    IS_SOURCE_OF("https://vocabulary.raid.org/relatedRaid.type.schema/199"),
    IS_DERIVED_FROM("https://vocabulary.raid.org/relatedRaid.type.schema/200"),
    HAS_PART("https://vocabulary.raid.org/relatedRaid.type.schema/201"),
    IS_PART_OF("https://vocabulary.raid.org/relatedRaid.type.schema/202"),
    IS_CONTINUED_BY("https://vocabulary.raid.org/relatedRaid.type.schema/203"),
    CONTINUES("https://vocabulary.raid.org/relatedRaid.type.schema/204"),
    IS_OBSOLETED_BY("https://vocabulary.raid.org/relatedRaid.type.schema/205");

    private final String uri;
}
