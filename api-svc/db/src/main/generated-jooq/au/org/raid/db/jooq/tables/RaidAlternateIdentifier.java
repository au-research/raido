/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.RaidAlternateIdentifierRecord;

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
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaidAlternateIdentifier extends TableImpl<RaidAlternateIdentifierRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.raid_alternate_identifier</code>
     */
    public static final RaidAlternateIdentifier RAID_ALTERNATE_IDENTIFIER = new RaidAlternateIdentifier();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RaidAlternateIdentifierRecord> getRecordType() {
        return RaidAlternateIdentifierRecord.class;
    }

    /**
     * The column <code>api_svc.raid_alternate_identifier.handle</code>.
     */
    public final TableField<RaidAlternateIdentifierRecord, String> HANDLE = createField(DSL.name("handle"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>api_svc.raid_alternate_identifier.id</code>.
     */
    public final TableField<RaidAlternateIdentifierRecord, String> ID = createField(DSL.name("id"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>api_svc.raid_alternate_identifier.type</code>.
     */
    public final TableField<RaidAlternateIdentifierRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR.nullable(false), this, "");

    private RaidAlternateIdentifier(Name alias, Table<RaidAlternateIdentifierRecord> aliased) {
        this(alias, aliased, null);
    }

    private RaidAlternateIdentifier(Name alias, Table<RaidAlternateIdentifierRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.raid_alternate_identifier</code> table
     * reference
     */
    public RaidAlternateIdentifier(String alias) {
        this(DSL.name(alias), RAID_ALTERNATE_IDENTIFIER);
    }

    /**
     * Create an aliased <code>api_svc.raid_alternate_identifier</code> table
     * reference
     */
    public RaidAlternateIdentifier(Name alias) {
        this(alias, RAID_ALTERNATE_IDENTIFIER);
    }

    /**
     * Create a <code>api_svc.raid_alternate_identifier</code> table reference
     */
    public RaidAlternateIdentifier() {
        this(DSL.name("raid_alternate_identifier"), null);
    }

    public <O extends Record> RaidAlternateIdentifier(Table<O> child, ForeignKey<O, RaidAlternateIdentifierRecord> key) {
        super(child, key, RAID_ALTERNATE_IDENTIFIER);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public UniqueKey<RaidAlternateIdentifierRecord> getPrimaryKey() {
        return Keys.RAID_ALTERNATE_IDENTIFIER_PKEY;
    }

    @Override
    public List<ForeignKey<RaidAlternateIdentifierRecord, ?>> getReferences() {
        return Arrays.asList(Keys.RAID_ALTERNATE_IDENTIFIER__RAID_ALTERNATE_IDENTIFIER_HANDLE_FKEY);
    }

    private transient Raid _raid;

    /**
     * Get the implicit join path to the <code>api_svc.raid</code> table.
     */
    public Raid raid() {
        if (_raid == null)
            _raid = new Raid(this, Keys.RAID_ALTERNATE_IDENTIFIER__RAID_ALTERNATE_IDENTIFIER_HANDLE_FKEY);

        return _raid;
    }

    @Override
    public RaidAlternateIdentifier as(String alias) {
        return new RaidAlternateIdentifier(DSL.name(alias), this);
    }

    @Override
    public RaidAlternateIdentifier as(Name alias) {
        return new RaidAlternateIdentifier(alias, this);
    }

    @Override
    public RaidAlternateIdentifier as(Table<?> alias) {
        return new RaidAlternateIdentifier(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidAlternateIdentifier rename(String name) {
        return new RaidAlternateIdentifier(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidAlternateIdentifier rename(Name name) {
        return new RaidAlternateIdentifier(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidAlternateIdentifier rename(Table<?> name) {
        return new RaidAlternateIdentifier(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}