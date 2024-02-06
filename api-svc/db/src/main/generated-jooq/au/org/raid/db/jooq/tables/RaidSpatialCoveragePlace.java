/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.RaidSpatialCoveragePlaceRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaidSpatialCoveragePlace extends TableImpl<RaidSpatialCoveragePlaceRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of
     * <code>api_svc.raid_spatial_coverage_place</code>
     */
    public static final RaidSpatialCoveragePlace RAID_SPATIAL_COVERAGE_PLACE = new RaidSpatialCoveragePlace();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RaidSpatialCoveragePlaceRecord> getRecordType() {
        return RaidSpatialCoveragePlaceRecord.class;
    }

    /**
     * The column
     * <code>api_svc.raid_spatial_coverage_place.raid_spatial_coverage_id</code>.
     */
    public final TableField<RaidSpatialCoveragePlaceRecord, Integer> RAID_SPATIAL_COVERAGE_ID = createField(DSL.name("raid_spatial_coverage_id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>api_svc.raid_spatial_coverage_place.place</code>.
     */
    public final TableField<RaidSpatialCoveragePlaceRecord, String> PLACE = createField(DSL.name("place"), SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>api_svc.raid_spatial_coverage_place.language_id</code>.
     */
    public final TableField<RaidSpatialCoveragePlaceRecord, Integer> LANGUAGE_ID = createField(DSL.name("language_id"), SQLDataType.INTEGER, this, "");

    private RaidSpatialCoveragePlace(Name alias, Table<RaidSpatialCoveragePlaceRecord> aliased) {
        this(alias, aliased, null);
    }

    private RaidSpatialCoveragePlace(Name alias, Table<RaidSpatialCoveragePlaceRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.raid_spatial_coverage_place</code> table
     * reference
     */
    public RaidSpatialCoveragePlace(String alias) {
        this(DSL.name(alias), RAID_SPATIAL_COVERAGE_PLACE);
    }

    /**
     * Create an aliased <code>api_svc.raid_spatial_coverage_place</code> table
     * reference
     */
    public RaidSpatialCoveragePlace(Name alias) {
        this(alias, RAID_SPATIAL_COVERAGE_PLACE);
    }

    /**
     * Create a <code>api_svc.raid_spatial_coverage_place</code> table reference
     */
    public RaidSpatialCoveragePlace() {
        this(DSL.name("raid_spatial_coverage_place"), null);
    }

    public <O extends Record> RaidSpatialCoveragePlace(Table<O> child, ForeignKey<O, RaidSpatialCoveragePlaceRecord> key) {
        super(child, key, RAID_SPATIAL_COVERAGE_PLACE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public List<ForeignKey<RaidSpatialCoveragePlaceRecord, ?>> getReferences() {
        return Arrays.asList(Keys.RAID_SPATIAL_COVERAGE_PLACE__RAID_SPATIAL_COVERAGE_PLACE_RAID_SPATIAL_COVERAGE_ID_FKEY, Keys.RAID_SPATIAL_COVERAGE_PLACE__RAID_SPATIAL_COVERAGE_PLACE_LANGUAGE_ID_FKEY);
    }

    private transient RaidSpatialCoverage _raidSpatialCoverage;
    private transient Language _language;

    /**
     * Get the implicit join path to the
     * <code>api_svc.raid_spatial_coverage</code> table.
     */
    public RaidSpatialCoverage raidSpatialCoverage() {
        if (_raidSpatialCoverage == null)
            _raidSpatialCoverage = new RaidSpatialCoverage(this, Keys.RAID_SPATIAL_COVERAGE_PLACE__RAID_SPATIAL_COVERAGE_PLACE_RAID_SPATIAL_COVERAGE_ID_FKEY);

        return _raidSpatialCoverage;
    }

    /**
     * Get the implicit join path to the <code>api_svc.language</code> table.
     */
    public Language language() {
        if (_language == null)
            _language = new Language(this, Keys.RAID_SPATIAL_COVERAGE_PLACE__RAID_SPATIAL_COVERAGE_PLACE_LANGUAGE_ID_FKEY);

        return _language;
    }

    @Override
    public RaidSpatialCoveragePlace as(String alias) {
        return new RaidSpatialCoveragePlace(DSL.name(alias), this);
    }

    @Override
    public RaidSpatialCoveragePlace as(Name alias) {
        return new RaidSpatialCoveragePlace(alias, this);
    }

    @Override
    public RaidSpatialCoveragePlace as(Table<?> alias) {
        return new RaidSpatialCoveragePlace(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidSpatialCoveragePlace rename(String name) {
        return new RaidSpatialCoveragePlace(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidSpatialCoveragePlace rename(Name name) {
        return new RaidSpatialCoveragePlace(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidSpatialCoveragePlace rename(Table<?> name) {
        return new RaidSpatialCoveragePlace(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super Integer, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super Integer, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
