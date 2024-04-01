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
    SUBJECT_SCHEMA_URI("https://vocabs.ardc.edu.au/viewById/316"),
    ACCESS_TYPE_OPEN("https://vocabularies.coar-repositories.org/access_rights/c_abf2/"),
    ACCESS_TYPE_EMBARGOED("https://vocabularies.coar-repositories.org/access_rights/c_f1cf/"),
    ACCESS_TYPE_SCHEMA_URI("https://vocabularies.coar-repositories.org/access_rights/"),
    PRIMARY_DESCRIPTION_TYPE("https://vocabulary.raid.org/description.type.schema/318"),
    ALTERNATIVE_DESCRIPTION_TYPE("https://vocabulary.raid.org/description.type.schema/319"),
    DESCRIPTION_TYPE_SCHEMA("https://vocabulary.raid.org/description.type.schema/320"),
    PRIMARY_TITLE_TYPE("https://vocabulary.raid.org/title.type.schema/5"),
    TITLE_TYPE_SCHEMA("https://vocabulary.raid.org/title.type.schema/376"),
    PRINCIPAL_INVESTIGATOR_CONTRIBUTOR_POSITION_ROLE("https://vocabulary.raid.org/contributor.position.schema/307"),

    CONTRIBUTOR_POSITION_SCHEMA_URI("https://vocabulary.raid.org/contributor.position.schema/305"),
    CONTRIBUTOR_ROLE_SCHEMA_URI("https://credit.niso.org"),

    LEAD_RESEARCH_ORGANISATION_ROLE("https://vocabulary.raid.org/organisation.role.schema/182"),
    ORGANISATION_ROLE_SCHEMA_URI("https://vocabulary.raid.org/organisation.role.schema/359"),

    LANGUAGE_SCHEMA("https://www.iso.org/standard/74575.html"),

    RAID_SCHEMA_URI("https://raid.org/");

    private final String uri;
}
