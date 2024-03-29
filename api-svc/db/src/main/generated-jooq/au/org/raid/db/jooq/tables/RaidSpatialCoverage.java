/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.RaidSpatialCoverageRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function4;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaidSpatialCoverage extends TableImpl<RaidSpatialCoverageRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.raid_spatial_coverage</code>
     */
    public static final RaidSpatialCoverage RAID_SPATIAL_COVERAGE = new RaidSpatialCoverage();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RaidSpatialCoverageRecord> getRecordType() {
        return RaidSpatialCoverageRecord.class;
    }

    /**
     * The column <code>api_svc.raid_spatial_coverage.id</code>.
     */
    public final TableField<RaidSpatialCoverageRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.raid_spatial_coverage.handle</code>.
     */
    public final TableField<RaidSpatialCoverageRecord, String> HANDLE = createField(DSL.name("handle"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>api_svc.raid_spatial_coverage.uri</code>.
     */
    public final TableField<RaidSpatialCoverageRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>api_svc.raid_spatial_coverage.schema_id</code>.
     */
    public final TableField<RaidSpatialCoverageRecord, Integer> SCHEMA_ID = createField(DSL.name("schema_id"), SQLDataType.INTEGER.nullable(false), this, "");

    private RaidSpatialCoverage(Name alias, Table<RaidSpatialCoverageRecord> aliased) {
        this(alias, aliased, null);
    }

    private RaidSpatialCoverage(Name alias, Table<RaidSpatialCoverageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.raid_spatial_coverage</code> table
     * reference
     */
    public RaidSpatialCoverage(String alias) {
        this(DSL.name(alias), RAID_SPATIAL_COVERAGE);
    }

    /**
     * Create an aliased <code>api_svc.raid_spatial_coverage</code> table
     * reference
     */
    public RaidSpatialCoverage(Name alias) {
        this(alias, RAID_SPATIAL_COVERAGE);
    }

    /**
     * Create a <code>api_svc.raid_spatial_coverage</code> table reference
     */
    public RaidSpatialCoverage() {
        this(DSL.name("raid_spatial_coverage"), null);
    }

    public <O extends Record> RaidSpatialCoverage(Table<O> child, ForeignKey<O, RaidSpatialCoverageRecord> key) {
        super(child, key, RAID_SPATIAL_COVERAGE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<RaidSpatialCoverageRecord, Integer> getIdentity() {
        return (Identity<RaidSpatialCoverageRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<RaidSpatialCoverageRecord> getPrimaryKey() {
        return Keys.RAID_SPATIAL_COVERAGE_PKEY;
    }

    @Override
    public List<ForeignKey<RaidSpatialCoverageRecord, ?>> getReferences() {
        return Arrays.asList(Keys.RAID_SPATIAL_COVERAGE__RAID_SPATIAL_COVERAGE_HANDLE_FKEY, Keys.RAID_SPATIAL_COVERAGE__RAID_SPATIAL_COVERAGE_SCHEMA_ID_FKEY);
    }

    private transient Raid _raid;
    private transient SpatialCoverageSchema _spatialCoverageSchema;

    /**
     * Get the implicit join path to the <code>api_svc.raid</code> table.
     */
    public Raid raid() {
        if (_raid == null)
            _raid = new Raid(this, Keys.RAID_SPATIAL_COVERAGE__RAID_SPATIAL_COVERAGE_HANDLE_FKEY);

        return _raid;
    }

    /**
     * Get the implicit join path to the
     * <code>api_svc.spatial_coverage_schema</code> table.
     */
    public SpatialCoverageSchema spatialCoverageSchema() {
        if (_spatialCoverageSchema == null)
            _spatialCoverageSchema = new SpatialCoverageSchema(this, Keys.RAID_SPATIAL_COVERAGE__RAID_SPATIAL_COVERAGE_SCHEMA_ID_FKEY);

        return _spatialCoverageSchema;
    }

    @Override
    public RaidSpatialCoverage as(String alias) {
        return new RaidSpatialCoverage(DSL.name(alias), this);
    }

    @Override
    public RaidSpatialCoverage as(Name alias) {
        return new RaidSpatialCoverage(alias, this);
    }

    @Override
    public RaidSpatialCoverage as(Table<?> alias) {
        return new RaidSpatialCoverage(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidSpatialCoverage rename(String name) {
        return new RaidSpatialCoverage(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidSpatialCoverage rename(Name name) {
        return new RaidSpatialCoverage(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidSpatialCoverage rename(Table<?> name) {
        return new RaidSpatialCoverage(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, String, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function4<? super Integer, ? super String, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function4<? super Integer, ? super String, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
