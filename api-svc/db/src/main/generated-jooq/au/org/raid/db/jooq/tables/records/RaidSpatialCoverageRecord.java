/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables.records;


import au.org.raid.db.jooq.tables.RaidSpatialCoverage;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaidSpatialCoverageRecord extends UpdatableRecordImpl<RaidSpatialCoverageRecord> implements Record5<String, String, Integer, String, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>api_svc.raid_spatial_coverage.raid_name</code>.
     */
    public RaidSpatialCoverageRecord setRaidName(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid_spatial_coverage.raid_name</code>.
     */
    public String getRaidName() {
        return (String) get(0);
    }

    /**
     * Setter for <code>api_svc.raid_spatial_coverage.id</code>.
     */
    public RaidSpatialCoverageRecord setId(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid_spatial_coverage.id</code>.
     */
    public String getId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>api_svc.raid_spatial_coverage.schema_id</code>.
     */
    public RaidSpatialCoverageRecord setSchemaId(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid_spatial_coverage.schema_id</code>.
     */
    public Integer getSchemaId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>api_svc.raid_spatial_coverage.place</code>.
     */
    public RaidSpatialCoverageRecord setPlace(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid_spatial_coverage.place</code>.
     */
    public String getPlace() {
        return (String) get(3);
    }

    /**
     * Setter for <code>api_svc.raid_spatial_coverage.language_id</code>.
     */
    public RaidSpatialCoverageRecord setLanguageId(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid_spatial_coverage.language_id</code>.
     */
    public Integer getLanguageId() {
        return (Integer) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record3<String, String, Integer> key() {
        return (Record3) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, String, Integer, String, Integer> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<String, String, Integer, String, Integer> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return RaidSpatialCoverage.RAID_SPATIAL_COVERAGE.RAID_NAME;
    }

    @Override
    public Field<String> field2() {
        return RaidSpatialCoverage.RAID_SPATIAL_COVERAGE.ID;
    }

    @Override
    public Field<Integer> field3() {
        return RaidSpatialCoverage.RAID_SPATIAL_COVERAGE.SCHEMA_ID;
    }

    @Override
    public Field<String> field4() {
        return RaidSpatialCoverage.RAID_SPATIAL_COVERAGE.PLACE;
    }

    @Override
    public Field<Integer> field5() {
        return RaidSpatialCoverage.RAID_SPATIAL_COVERAGE.LANGUAGE_ID;
    }

    @Override
    public String component1() {
        return getRaidName();
    }

    @Override
    public String component2() {
        return getId();
    }

    @Override
    public Integer component3() {
        return getSchemaId();
    }

    @Override
    public String component4() {
        return getPlace();
    }

    @Override
    public Integer component5() {
        return getLanguageId();
    }

    @Override
    public String value1() {
        return getRaidName();
    }

    @Override
    public String value2() {
        return getId();
    }

    @Override
    public Integer value3() {
        return getSchemaId();
    }

    @Override
    public String value4() {
        return getPlace();
    }

    @Override
    public Integer value5() {
        return getLanguageId();
    }

    @Override
    public RaidSpatialCoverageRecord value1(String value) {
        setRaidName(value);
        return this;
    }

    @Override
    public RaidSpatialCoverageRecord value2(String value) {
        setId(value);
        return this;
    }

    @Override
    public RaidSpatialCoverageRecord value3(Integer value) {
        setSchemaId(value);
        return this;
    }

    @Override
    public RaidSpatialCoverageRecord value4(String value) {
        setPlace(value);
        return this;
    }

    @Override
    public RaidSpatialCoverageRecord value5(Integer value) {
        setLanguageId(value);
        return this;
    }

    @Override
    public RaidSpatialCoverageRecord values(String value1, String value2, Integer value3, String value4, Integer value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RaidSpatialCoverageRecord
     */
    public RaidSpatialCoverageRecord() {
        super(RaidSpatialCoverage.RAID_SPATIAL_COVERAGE);
    }

    /**
     * Create a detached, initialised RaidSpatialCoverageRecord
     */
    public RaidSpatialCoverageRecord(String raidName, String id, Integer schemaId, String place, Integer languageId) {
        super(RaidSpatialCoverage.RAID_SPATIAL_COVERAGE);

        setRaidName(raidName);
        setId(id);
        setSchemaId(schemaId);
        setPlace(place);
        setLanguageId(languageId);
    }
}
