package au.org.raid.api.repository;

import au.org.raid.api.endpoint.Constant;
import au.org.raid.db.jooq.tables.records.RaidRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static au.org.raid.db.jooq.tables.Contributor.CONTRIBUTOR;
import static au.org.raid.db.jooq.tables.Raid.RAID;
import static au.org.raid.db.jooq.tables.RaidContributor.RAID_CONTRIBUTOR;

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

    public int update(final RaidRecord record) {
        return dslContext.update(RAID)
                .set(RAID.SERVICE_POINT_ID, record.getServicePointId())
                .set(RAID.METADATA, record.getMetadata())
                .set(RAID.METADATA_SCHEMA, record.getMetadataSchema())
                .set(RAID.START_DATE, record.getStartDate())
                .set(RAID.DATE_CREATED, LocalDateTime.now())
                .set(RAID.CONFIDENTIAL, record.getConfidential())
                .set(RAID.VERSION, record.getVersion())
                .set(RAID.START_DATE_STRING, record.getStartDateString())
                .set(RAID.END_DATE, record.getEndDate())
                .set(RAID.LICENSE, record.getLicense())
                .set(RAID.ACCESS_TYPE_ID, record.getAccessTypeId())
                .set(RAID.EMBARGO_EXPIRY, record.getEmbargoExpiry())
                .set(RAID.ACCESS_STATEMENT, record.getAccessStatement())
                .set(RAID.ACCESS_STATEMENT_LANGUAGE_ID, record.getAccessStatementLanguageId())
                .set(RAID.SCHEMA_URI, record.getSchemaUri())
                .set(RAID.REGISTRATION_AGENCY_ORGANISATION_ID, record.getRegistrationAgencyOrganisationId())
                .set(RAID.OWNER_ORGANISATION_ID, record.getOwnerOrganisationId())
                .where(RAID.HANDLE.eq(record.getHandle()))
                .execute();
    }

    public Optional<RaidRecord> findByHandle(final String handle) {
        return dslContext.selectFrom(RAID)
                .where(RAID.HANDLE.eq(handle))
                .fetchOptional();
    }

    public List<RaidRecord> findAllByServicePointId(final Long servicePointId) {
        return dslContext.selectFrom(RAID)
                .where(RAID.SERVICE_POINT_ID.eq(servicePointId))
                .orderBy(RAID.DATE_CREATED.desc())
                .limit(Constant.MAX_EXPERIMENTAL_RECORDS)
                .fetch();
    }

    public List<RaidRecord> findAllByServicePointIdOrNotConfidential(Long servicePointId) {
        return dslContext.selectFrom(RAID)
                .where(
                        RAID.SERVICE_POINT_ID.eq(servicePointId).or(RAID.CONFIDENTIAL.equal(false))
                )
                .orderBy(RAID.DATE_CREATED.desc())
                .limit(Constant.MAX_EXPERIMENTAL_RECORDS)
                .fetch();
    }

    public List<RaidRecord> findAllByContributorOrcid(final String orcid) {
        return dslContext.select()
                .from(RAID)
                .join(RAID_CONTRIBUTOR)
                .on(RAID.HANDLE.eq(RAID_CONTRIBUTOR.HANDLE))
                .join(CONTRIBUTOR)
                .on(RAID_CONTRIBUTOR.CONTRIBUTOR_ID.eq(CONTRIBUTOR.ID))
                .where(
                        CONTRIBUTOR.PID.eq(orcid)
                )
                .fetchInto(RaidRecord.class);
    }


}