package au.org.raid.api.vocabularies.datacite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelationType {
    IS_CITED_BY("IsCitedBy"),
    CITES("Cites"),
    IS_SUPPLEMENT_TO("IsSupplementTo"),
    IS_SUPPLEMENTED_BY("IsSupplementedBy"),
    IS_CONTINUED_BY("IsContinuedBy"),
    CONTINUES("Continues"),
    IS_DESCRIBED_BY("IsDescribedBy"),
    DESCRIBES("Describes"),
    HAS_METADATA("HasMetadata"),
    IS_METADATA_FOR("IsMetadataFor"),
    HAS_VERSION("HasVersion"),
    IS_VERSION_OF("IsVersionOf"),
    IS_PREVIOUS_VERSION_OF("IsPreviousVersionOf"),
    IS_PART_OF("IsPartOf"),
    HAS_PART("HasPart"),
    IS_PUBLISHED_IN("IsPublishedIn"),
    IS_REFERENCED_BY("IsReferencedBy"),
    REFERENCES("References"),
    IS_DOCUMENTED_BY("IsDocumentedBy"),
    DOCUMENTS("Documents"),
    IS_COMPILED_BY("IsCompiledBy"),
    COMPILES("Compiles"),
    IS_VARIANT_FORM_OF("IsVariantFormOf"),
    IS_ORIGINAL_FORM_OF("IsOriginalFormOf"),
    IS_IDENTICAL_TO("IsIdenticalTo"),
    IS_REVIEWED_BY("IsReviewedBy"),
    REVIEWS("Reviews"),
    IS_DERIVED_FROM("IsDerivedFrom"),
    IS_SOURCE_OF("IsSourceOf"),
    IS_REQUIRED_BY("IsRequiredBy"),
    REQUIRES("Requires"),
    IS_OBSOLETED_BY("IsObsoletedBy"),
    OBSOLETES("Obsoletes"),
    IS_COLLECTED_BY("IsCollectedBy"),
    COLLECTS("Collects");



    private final String name;
}
