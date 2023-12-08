/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables.records;


import au.org.raid.db.jooq.enums.Metaschema;
import au.org.raid.db.jooq.tables.Raid;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Record1;
import org.jooq.Record20;
import org.jooq.Row20;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaidRecord extends UpdatableRecordImpl<RaidRecord> implements Record20<String, Long, String, Integer, String, Boolean, Metaschema, JSONB, LocalDate, LocalDateTime, Integer, String, String, Integer, String, String, Integer, String, Integer, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>api_svc.raid.handle</code>. Holds the handle (i.e. just
     * prefix/suffix) not the URL.  Usually quite  short in production, but the
     * max length is set to accommodate int and load testing.
     */
    public RaidRecord setHandle(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.handle</code>. Holds the handle (i.e. just
     * prefix/suffix) not the URL.  Usually quite  short in production, but the
     * max length is set to accommodate int and load testing.
     */
    public String getHandle() {
        return (String) get(0);
    }

    /**
     * Setter for <code>api_svc.raid.service_point_id</code>.
     */
    public RaidRecord setServicePointId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.service_point_id</code>.
     */
    public Long getServicePointId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>api_svc.raid.url</code>. The value that we set as the
     * `URL` property via ARDC APIDS.
     *   Example: `https://demo.raido-infra.com/raid/123.456/789`. 
     *   The global handle regisrty url (e.g.
     * `https://hdl.handle.net/123.456/789`) 
     *   will redirect to this value.
     */
    public RaidRecord setUrl(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.url</code>. The value that we set as the
     * `URL` property via ARDC APIDS.
     *   Example: `https://demo.raido-infra.com/raid/123.456/789`. 
     *   The global handle regisrty url (e.g.
     * `https://hdl.handle.net/123.456/789`) 
     *   will redirect to this value.
     */
    public String getUrl() {
        return (String) get(2);
    }

    /**
     * Setter for <code>api_svc.raid.url_index</code>. The `index` of the URL
     * property in APIDS. This can be different if we change
     *   how we mint URL values via APIDS.
     */
    public RaidRecord setUrlIndex(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.url_index</code>. The `index` of the URL
     * property in APIDS. This can be different if we change
     *   how we mint URL values via APIDS.
     */
    public Integer getUrlIndex() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>api_svc.raid.primary_title</code>.
     */
    public RaidRecord setPrimaryTitle(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.primary_title</code>.
     */
    public String getPrimaryTitle() {
        return (String) get(4);
    }

    /**
     * Setter for <code>api_svc.raid.confidential</code>.
     */
    public RaidRecord setConfidential(Boolean value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.confidential</code>.
     */
    public Boolean getConfidential() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>api_svc.raid.metadata_schema</code>. Identifies the
     * structure of the data in the metadata column
     */
    public RaidRecord setMetadataSchema(Metaschema value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.metadata_schema</code>. Identifies the
     * structure of the data in the metadata column
     */
    public Metaschema getMetadataSchema() {
        return (Metaschema) get(6);
    }

    /**
     * Setter for <code>api_svc.raid.metadata</code>.
     */
    public RaidRecord setMetadata(JSONB value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.metadata</code>.
     */
    public JSONB getMetadata() {
        return (JSONB) get(7);
    }

    /**
     * Setter for <code>api_svc.raid.start_date</code>.
     */
    public RaidRecord setStartDate(LocalDate value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.start_date</code>.
     */
    public LocalDate getStartDate() {
        return (LocalDate) get(8);
    }

    /**
     * Setter for <code>api_svc.raid.date_created</code>.
     */
    public RaidRecord setDateCreated(LocalDateTime value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.date_created</code>.
     */
    public LocalDateTime getDateCreated() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>api_svc.raid.version</code>.
     */
    public RaidRecord setVersion(Integer value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.version</code>.
     */
    public Integer getVersion() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>api_svc.raid.end_date</code>.
     */
    public RaidRecord setEndDate(String value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.end_date</code>.
     */
    public String getEndDate() {
        return (String) get(11);
    }

    /**
     * Setter for <code>api_svc.raid.license</code>.
     */
    public RaidRecord setLicense(String value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.license</code>.
     */
    public String getLicense() {
        return (String) get(12);
    }

    /**
     * Setter for <code>api_svc.raid.access_type_id</code>.
     */
    public RaidRecord setAccessTypeId(Integer value) {
        set(13, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.access_type_id</code>.
     */
    public Integer getAccessTypeId() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>api_svc.raid.embargo_expiry</code>.
     */
    public RaidRecord setEmbargoExpiry(String value) {
        set(14, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.embargo_expiry</code>.
     */
    public String getEmbargoExpiry() {
        return (String) get(14);
    }

    /**
     * Setter for <code>api_svc.raid.access_statement</code>.
     */
    public RaidRecord setAccessStatement(String value) {
        set(15, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.access_statement</code>.
     */
    public String getAccessStatement() {
        return (String) get(15);
    }

    /**
     * Setter for <code>api_svc.raid.access_statement_language_id</code>.
     */
    public RaidRecord setAccessStatementLanguageId(Integer value) {
        set(16, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.access_statement_language_id</code>.
     */
    public Integer getAccessStatementLanguageId() {
        return (Integer) get(16);
    }

    /**
     * Setter for <code>api_svc.raid.schema_uri</code>.
     */
    public RaidRecord setSchemaUri(String value) {
        set(17, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.schema_uri</code>.
     */
    public String getSchemaUri() {
        return (String) get(17);
    }

    /**
     * Setter for <code>api_svc.raid.registration_agency_organisation_id</code>.
     */
    public RaidRecord setRegistrationAgencyOrganisationId(Integer value) {
        set(18, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.registration_agency_organisation_id</code>.
     */
    public Integer getRegistrationAgencyOrganisationId() {
        return (Integer) get(18);
    }

    /**
     * Setter for <code>api_svc.raid.owner_organisation_id</code>.
     */
    public RaidRecord setOwnerOrganisationId(Integer value) {
        set(19, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid.owner_organisation_id</code>.
     */
    public Integer getOwnerOrganisationId() {
        return (Integer) get(19);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record20 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row20<String, Long, String, Integer, String, Boolean, Metaschema, JSONB, LocalDate, LocalDateTime, Integer, String, String, Integer, String, String, Integer, String, Integer, Integer> fieldsRow() {
        return (Row20) super.fieldsRow();
    }

    @Override
    public Row20<String, Long, String, Integer, String, Boolean, Metaschema, JSONB, LocalDate, LocalDateTime, Integer, String, String, Integer, String, String, Integer, String, Integer, Integer> valuesRow() {
        return (Row20) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Raid.RAID.HANDLE;
    }

    @Override
    public Field<Long> field2() {
        return Raid.RAID.SERVICE_POINT_ID;
    }

    @Override
    public Field<String> field3() {
        return Raid.RAID.URL;
    }

    @Override
    public Field<Integer> field4() {
        return Raid.RAID.URL_INDEX;
    }

    @Override
    public Field<String> field5() {
        return Raid.RAID.PRIMARY_TITLE;
    }

    @Override
    public Field<Boolean> field6() {
        return Raid.RAID.CONFIDENTIAL;
    }

    @Override
    public Field<Metaschema> field7() {
        return Raid.RAID.METADATA_SCHEMA;
    }

    @Override
    public Field<JSONB> field8() {
        return Raid.RAID.METADATA;
    }

    @Override
    public Field<LocalDate> field9() {
        return Raid.RAID.START_DATE;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return Raid.RAID.DATE_CREATED;
    }

    @Override
    public Field<Integer> field11() {
        return Raid.RAID.VERSION;
    }

    @Override
    public Field<String> field12() {
        return Raid.RAID.END_DATE;
    }

    @Override
    public Field<String> field13() {
        return Raid.RAID.LICENSE;
    }

    @Override
    public Field<Integer> field14() {
        return Raid.RAID.ACCESS_TYPE_ID;
    }

    @Override
    public Field<String> field15() {
        return Raid.RAID.EMBARGO_EXPIRY;
    }

    @Override
    public Field<String> field16() {
        return Raid.RAID.ACCESS_STATEMENT;
    }

    @Override
    public Field<Integer> field17() {
        return Raid.RAID.ACCESS_STATEMENT_LANGUAGE_ID;
    }

    @Override
    public Field<String> field18() {
        return Raid.RAID.SCHEMA_URI;
    }

    @Override
    public Field<Integer> field19() {
        return Raid.RAID.REGISTRATION_AGENCY_ORGANISATION_ID;
    }

    @Override
    public Field<Integer> field20() {
        return Raid.RAID.OWNER_ORGANISATION_ID;
    }

    @Override
    public String component1() {
        return getHandle();
    }

    @Override
    public Long component2() {
        return getServicePointId();
    }

    @Override
    public String component3() {
        return getUrl();
    }

    @Override
    public Integer component4() {
        return getUrlIndex();
    }

    @Override
    public String component5() {
        return getPrimaryTitle();
    }

    @Override
    public Boolean component6() {
        return getConfidential();
    }

    @Override
    public Metaschema component7() {
        return getMetadataSchema();
    }

    @Override
    public JSONB component8() {
        return getMetadata();
    }

    @Override
    public LocalDate component9() {
        return getStartDate();
    }

    @Override
    public LocalDateTime component10() {
        return getDateCreated();
    }

    @Override
    public Integer component11() {
        return getVersion();
    }

    @Override
    public String component12() {
        return getEndDate();
    }

    @Override
    public String component13() {
        return getLicense();
    }

    @Override
    public Integer component14() {
        return getAccessTypeId();
    }

    @Override
    public String component15() {
        return getEmbargoExpiry();
    }

    @Override
    public String component16() {
        return getAccessStatement();
    }

    @Override
    public Integer component17() {
        return getAccessStatementLanguageId();
    }

    @Override
    public String component18() {
        return getSchemaUri();
    }

    @Override
    public Integer component19() {
        return getRegistrationAgencyOrganisationId();
    }

    @Override
    public Integer component20() {
        return getOwnerOrganisationId();
    }

    @Override
    public String value1() {
        return getHandle();
    }

    @Override
    public Long value2() {
        return getServicePointId();
    }

    @Override
    public String value3() {
        return getUrl();
    }

    @Override
    public Integer value4() {
        return getUrlIndex();
    }

    @Override
    public String value5() {
        return getPrimaryTitle();
    }

    @Override
    public Boolean value6() {
        return getConfidential();
    }

    @Override
    public Metaschema value7() {
        return getMetadataSchema();
    }

    @Override
    public JSONB value8() {
        return getMetadata();
    }

    @Override
    public LocalDate value9() {
        return getStartDate();
    }

    @Override
    public LocalDateTime value10() {
        return getDateCreated();
    }

    @Override
    public Integer value11() {
        return getVersion();
    }

    @Override
    public String value12() {
        return getEndDate();
    }

    @Override
    public String value13() {
        return getLicense();
    }

    @Override
    public Integer value14() {
        return getAccessTypeId();
    }

    @Override
    public String value15() {
        return getEmbargoExpiry();
    }

    @Override
    public String value16() {
        return getAccessStatement();
    }

    @Override
    public Integer value17() {
        return getAccessStatementLanguageId();
    }

    @Override
    public String value18() {
        return getSchemaUri();
    }

    @Override
    public Integer value19() {
        return getRegistrationAgencyOrganisationId();
    }

    @Override
    public Integer value20() {
        return getOwnerOrganisationId();
    }

    @Override
    public RaidRecord value1(String value) {
        setHandle(value);
        return this;
    }

    @Override
    public RaidRecord value2(Long value) {
        setServicePointId(value);
        return this;
    }

    @Override
    public RaidRecord value3(String value) {
        setUrl(value);
        return this;
    }

    @Override
    public RaidRecord value4(Integer value) {
        setUrlIndex(value);
        return this;
    }

    @Override
    public RaidRecord value5(String value) {
        setPrimaryTitle(value);
        return this;
    }

    @Override
    public RaidRecord value6(Boolean value) {
        setConfidential(value);
        return this;
    }

    @Override
    public RaidRecord value7(Metaschema value) {
        setMetadataSchema(value);
        return this;
    }

    @Override
    public RaidRecord value8(JSONB value) {
        setMetadata(value);
        return this;
    }

    @Override
    public RaidRecord value9(LocalDate value) {
        setStartDate(value);
        return this;
    }

    @Override
    public RaidRecord value10(LocalDateTime value) {
        setDateCreated(value);
        return this;
    }

    @Override
    public RaidRecord value11(Integer value) {
        setVersion(value);
        return this;
    }

    @Override
    public RaidRecord value12(String value) {
        setEndDate(value);
        return this;
    }

    @Override
    public RaidRecord value13(String value) {
        setLicense(value);
        return this;
    }

    @Override
    public RaidRecord value14(Integer value) {
        setAccessTypeId(value);
        return this;
    }

    @Override
    public RaidRecord value15(String value) {
        setEmbargoExpiry(value);
        return this;
    }

    @Override
    public RaidRecord value16(String value) {
        setAccessStatement(value);
        return this;
    }

    @Override
    public RaidRecord value17(Integer value) {
        setAccessStatementLanguageId(value);
        return this;
    }

    @Override
    public RaidRecord value18(String value) {
        setSchemaUri(value);
        return this;
    }

    @Override
    public RaidRecord value19(Integer value) {
        setRegistrationAgencyOrganisationId(value);
        return this;
    }

    @Override
    public RaidRecord value20(Integer value) {
        setOwnerOrganisationId(value);
        return this;
    }

    @Override
    public RaidRecord values(String value1, Long value2, String value3, Integer value4, String value5, Boolean value6, Metaschema value7, JSONB value8, LocalDate value9, LocalDateTime value10, Integer value11, String value12, String value13, Integer value14, String value15, String value16, Integer value17, String value18, Integer value19, Integer value20) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        value20(value20);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RaidRecord
     */
    public RaidRecord() {
        super(Raid.RAID);
    }

    /**
     * Create a detached, initialised RaidRecord
     */
    public RaidRecord(String handle, Long servicePointId, String url, Integer urlIndex, String primaryTitle, Boolean confidential, Metaschema metadataSchema, JSONB metadata, LocalDate startDate, LocalDateTime dateCreated, Integer version, String endDate, String license, Integer accessTypeId, String embargoExpiry, String accessStatement, Integer accessStatementLanguageId, String schemaUri, Integer registrationAgencyOrganisationId, Integer ownerOrganisationId) {
        super(Raid.RAID);

        setHandle(handle);
        setServicePointId(servicePointId);
        setUrl(url);
        setUrlIndex(urlIndex);
        setPrimaryTitle(primaryTitle);
        setConfidential(confidential);
        setMetadataSchema(metadataSchema);
        setMetadata(metadata);
        setStartDate(startDate);
        setDateCreated(dateCreated);
        setVersion(version);
        setEndDate(endDate);
        setLicense(license);
        setAccessTypeId(accessTypeId);
        setEmbargoExpiry(embargoExpiry);
        setAccessStatement(accessStatement);
        setAccessStatementLanguageId(accessStatementLanguageId);
        setSchemaUri(schemaUri);
        setRegistrationAgencyOrganisationId(registrationAgencyOrganisationId);
        setOwnerOrganisationId(ownerOrganisationId);
    }
}