package au.org.raid.api.repository;

import au.org.raid.api.endpoint.Constant;
import au.org.raid.db.jooq.tables.records.RaidRecord;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static au.org.raid.db.jooq.tables.Contributor.CONTRIBUTOR;
import static au.org.raid.db.jooq.tables.ContributorPosition.CONTRIBUTOR_POSITION;
import static au.org.raid.db.jooq.tables.ContributorPositionSchema.CONTRIBUTOR_POSITION_SCHEMA;
import static au.org.raid.db.jooq.tables.ContributorRole.CONTRIBUTOR_ROLE;
import static au.org.raid.db.jooq.tables.ContributorRoleSchema.CONTRIBUTOR_ROLE_SCHEMA;
import static au.org.raid.db.jooq.tables.DescriptionType.DESCRIPTION_TYPE;
import static au.org.raid.db.jooq.tables.DescriptionTypeSchema.DESCRIPTION_TYPE_SCHEMA;
import static au.org.raid.db.jooq.tables.Organisation.ORGANISATION;
import static au.org.raid.db.jooq.tables.OrganisationRole.ORGANISATION_ROLE;
import static au.org.raid.db.jooq.tables.OrganisationRoleSchema.ORGANISATION_ROLE_SCHEMA;
import static au.org.raid.db.jooq.tables.Raid.RAID;
import static au.org.raid.db.jooq.tables.RaidAlternateIdentifier.RAID_ALTERNATE_IDENTIFIER;
import static au.org.raid.db.jooq.tables.RaidAlternateUrl.RAID_ALTERNATE_URL;
import static au.org.raid.db.jooq.tables.RaidContributor.RAID_CONTRIBUTOR;
import static au.org.raid.db.jooq.tables.RaidContributorPosition.RAID_CONTRIBUTOR_POSITION;
import static au.org.raid.db.jooq.tables.RaidContributorRole.RAID_CONTRIBUTOR_ROLE;
import static au.org.raid.db.jooq.tables.RaidDescription.RAID_DESCRIPTION;
import static au.org.raid.db.jooq.tables.RaidOrganisation.RAID_ORGANISATION;
import static au.org.raid.db.jooq.tables.RaidOrganisationRole.RAID_ORGANISATION_ROLE;
import static au.org.raid.db.jooq.tables.RaidRelatedObject.RAID_RELATED_OBJECT;
import static au.org.raid.db.jooq.tables.RaidRelatedObjectCategory.RAID_RELATED_OBJECT_CATEGORY;
import static au.org.raid.db.jooq.tables.RaidSpatialCoverage.RAID_SPATIAL_COVERAGE;
import static au.org.raid.db.jooq.tables.RaidSubject.RAID_SUBJECT;
import static au.org.raid.db.jooq.tables.RaidTitle.RAID_TITLE;
import static au.org.raid.db.jooq.tables.RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL;
import static au.org.raid.db.jooq.tables.RelatedObject.RELATED_OBJECT;
import static au.org.raid.db.jooq.tables.RelatedObjectCategory.RELATED_OBJECT_CATEGORY;
import static au.org.raid.db.jooq.tables.RelatedObjectCategorySchema.RELATED_OBJECT_CATEGORY_SCHEMA;
import static au.org.raid.db.jooq.tables.RelatedObjectType.RELATED_OBJECT_TYPE;
import static au.org.raid.db.jooq.tables.RelatedObjectTypeSchema.RELATED_OBJECT_TYPE_SCHEMA;
import static au.org.raid.db.jooq.tables.RelatedRaid.RELATED_RAID;
import static au.org.raid.db.jooq.tables.RelatedRaidType.RELATED_RAID_TYPE;
import static au.org.raid.db.jooq.tables.ServicePoint.SERVICE_POINT;
import static au.org.raid.db.jooq.tables.SpatialCoverageSchema.SPATIAL_COVERAGE_SCHEMA;
import static au.org.raid.db.jooq.tables.SubjectType.SUBJECT_TYPE;
import static au.org.raid.db.jooq.tables.SubjectTypeSchema.SUBJECT_TYPE_SCHEMA;
import static au.org.raid.db.jooq.tables.TitleType.TITLE_TYPE;
import static au.org.raid.db.jooq.tables.TitleTypeSchema.TITLE_TYPE_SCHEMA;
import static au.org.raid.db.jooq.tables.TraditionalKnowledgeLabel.TRADITIONAL_KNOWLEDGE_LABEL;
import static au.org.raid.db.jooq.tables.TraditionalKnowledgeLabelSchema.TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA;

@Repository
@RequiredArgsConstructor
public class RaidRepository {
    private final DSLContext dslContext;

    public RaidRecord insert(final RaidRecord raid) {
        return dslContext.insertInto(RAID)
                .set(RAID.HANDLE, raid.getHandle())
                .set(RAID.SERVICE_POINT_ID, raid.getServicePointId())
                .set(RAID.URL, raid.getUrl())
                .set(RAID.METADATA, raid.getMetadata())
                .set(RAID.METADATA_SCHEMA, raid.getMetadataSchema())
                .set(RAID.START_DATE, raid.getStartDate())
                .set(RAID.DATE_CREATED, LocalDateTime.now())
                .set(RAID.CONFIDENTIAL, raid.getConfidential())
                .set(RAID.VERSION, raid.getVersion())
                .set(RAID.START_DATE_STRING, raid.getStartDateString())
                .set(RAID.END_DATE, raid.getEndDate())
                .set(RAID.LICENSE, raid.getLicense())
                .set(RAID.ACCESS_TYPE_ID, raid.getAccessTypeId())
                .set(RAID.EMBARGO_EXPIRY, raid.getEmbargoExpiry())
                .set(RAID.ACCESS_STATEMENT, raid.getAccessStatement())
                .set(RAID.ACCESS_STATEMENT_LANGUAGE_ID, raid.getAccessStatementLanguageId())
                .set(RAID.SCHEMA_URI, raid.getSchemaUri())
                .set(RAID.REGISTRATION_AGENCY_ORGANISATION_ID, raid.getRegistrationAgencyOrganisationId())
                .set(RAID.OWNER_ORGANISATION_ID, raid.getOwnerOrganisationId())
                .returning()
                .fetchOne();
    }

    public int update(final RaidRecord raidRecord) {
        return dslContext.update(RAID)
                .set(RAID.METADATA, raidRecord.getMetadata())
                .set(RAID.METADATA_SCHEMA, raidRecord.getMetadataSchema())
                .set(RAID.START_DATE, raidRecord.getStartDate())
                .set(RAID.CONFIDENTIAL, raidRecord.getConfidential())
                .where(RAID.HANDLE.eq(raidRecord.getHandle()))
                .execute();
    }

    public int updateByHandleAndVersion(final RaidRecord raidRecord, final int version) {
        return dslContext.update(RAID)
                .set(RAID.METADATA, raidRecord.getMetadata())
                .set(RAID.METADATA_SCHEMA, raidRecord.getMetadataSchema())
                .set(RAID.START_DATE, raidRecord.getStartDate())
                .set(RAID.CONFIDENTIAL, raidRecord.getConfidential())
                .set(RAID.VERSION, raidRecord.getVersion())
                .where(RAID.HANDLE.eq(raidRecord.getHandle()))
                .and(RAID.VERSION.eq(version))
                .execute();
    }

    public Optional<RaidRecord> findByHandle(final String handle) {
        return dslContext.selectFrom(RAID)
                .where(RAID.HANDLE.eq(handle)).
                fetchOptional();
    }


    public Optional<RaidRecord> findByHandleAndVersion(final String handle, final int version) {
        return dslContext.select(RAID.fields())
                .from(RAID)
                .where(RAID.HANDLE.eq(handle)
                        .and(RAID.VERSION.eq(version))).
                fetchOptional(record -> new RaidRecord()
                        .setVersion(RAID.VERSION.getValue(record))
                        .setHandle(RAID.HANDLE.getValue(record))
                        .setServicePointId(RAID.SERVICE_POINT_ID.getValue(record))
                        .setUrl(RAID.URL.getValue(record))
                        .setMetadataSchema(RAID.METADATA_SCHEMA.getValue(record))
                        .setMetadata(RAID.METADATA.getValue(record))
                        .setDateCreated(RAID.DATE_CREATED.getValue(record))
                        .setStartDate(RAID.START_DATE.getValue(record))
                        .setConfidential(RAID.CONFIDENTIAL.getValue(record))
                );
    }

    public List<RaidRecord> findAllByServicePointId(final Long servicePointId) {
        return dslContext.select(RAID.fields()).
                select(SERVICE_POINT.fields()).
                from(RAID).join(SERVICE_POINT).onKey().
                where(
                        RAID.SERVICE_POINT_ID.eq(servicePointId)
                ).
                orderBy(RAID.DATE_CREATED.desc()).
                limit(Constant.MAX_EXPERIMENTAL_RECORDS).
                fetch(record -> new RaidRecord()
                        .setVersion(RAID.VERSION.getValue(record))
                        .setHandle(RAID.HANDLE.getValue(record))
                        .setServicePointId(RAID.SERVICE_POINT_ID.getValue(record))
                        .setUrl(RAID.URL.getValue(record))
                        .setMetadataSchema(RAID.METADATA_SCHEMA.getValue(record))
                        .setMetadata(RAID.METADATA.getValue(record))
                        .setDateCreated(RAID.DATE_CREATED.getValue(record))
                        .setStartDate(RAID.START_DATE.getValue(record))
                        .setConfidential(RAID.CONFIDENTIAL.getValue(record))
                );
    }

    public List<RaidRecord> findAllByServicePointOrNotConfidentialId(Long servicePointId) {
        return dslContext.select(RAID.fields())
                .from(RAID)
                .where(
                        RAID.SERVICE_POINT_ID.eq(servicePointId).or(RAID.CONFIDENTIAL.equal(false))
                )
                .orderBy(RAID.DATE_CREATED.desc())
                .limit(Constant.MAX_EXPERIMENTAL_RECORDS)
                .fetch(record -> new RaidRecord()
                        .setVersion(RAID.VERSION.getValue(record))
                        .setHandle(RAID.HANDLE.getValue(record))
                        .setServicePointId(RAID.SERVICE_POINT_ID.getValue(record))
                        .setUrl(RAID.URL.getValue(record))
                        .setMetadataSchema(RAID.METADATA_SCHEMA.getValue(record))
                        .setMetadata(RAID.METADATA.getValue(record))
                        .setDateCreated(RAID.DATE_CREATED.getValue(record))
                        .setStartDate(RAID.START_DATE.getValue(record))
                        .setConfidential(RAID.CONFIDENTIAL.getValue(record))
                );
    }
}