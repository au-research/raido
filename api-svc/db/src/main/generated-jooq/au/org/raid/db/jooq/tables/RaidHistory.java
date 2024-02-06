/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.RaidHistoryRecord;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.time.LocalDateTime;
import java.util.function.Function;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaidHistory extends TableImpl<RaidHistoryRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.raid_history</code>
     */
    public static final RaidHistory RAID_HISTORY = new RaidHistory();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RaidHistoryRecord> getRecordType() {
        return RaidHistoryRecord.class;
    }

    /**
     * The column <code>api_svc.raid_history.handle</code>.
     */
    public final TableField<RaidHistoryRecord, String> HANDLE = createField(DSL.name("handle"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>api_svc.raid_history.revision</code>.
     */
    public final TableField<RaidHistoryRecord, Integer> REVISION = createField(DSL.name("revision"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>api_svc.raid_history.change_type</code>.
     */
    public final TableField<RaidHistoryRecord, String> CHANGE_TYPE = createField(DSL.name("change_type"), SQLDataType.VARCHAR(8).nullable(false), this, "");

    /**
     * The column <code>api_svc.raid_history.diff</code>.
     */
    public final TableField<RaidHistoryRecord, String> DIFF = createField(DSL.name("diff"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>api_svc.raid_history.created</code>.
     */
    public final TableField<RaidHistoryRecord, LocalDateTime> CREATED = createField(DSL.name("created"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    private RaidHistory(Name alias, Table<RaidHistoryRecord> aliased) {
        this(alias, aliased, null);
    }

    private RaidHistory(Name alias, Table<RaidHistoryRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.raid_history</code> table reference
     */
    public RaidHistory(String alias) {
        this(DSL.name(alias), RAID_HISTORY);
    }

    /**
     * Create an aliased <code>api_svc.raid_history</code> table reference
     */
    public RaidHistory(Name alias) {
        this(alias, RAID_HISTORY);
    }

    /**
     * Create a <code>api_svc.raid_history</code> table reference
     */
    public RaidHistory() {
        this(DSL.name("raid_history"), null);
    }

    public <O extends Record> RaidHistory(Table<O> child, ForeignKey<O, RaidHistoryRecord> key) {
        super(child, key, RAID_HISTORY);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public UniqueKey<RaidHistoryRecord> getPrimaryKey() {
        return Keys.RAID_HISTORY_PKEY;
    }

    @Override
    public RaidHistory as(String alias) {
        return new RaidHistory(DSL.name(alias), this);
    }

    @Override
    public RaidHistory as(Name alias) {
        return new RaidHistory(alias, this);
    }

    @Override
    public RaidHistory as(Table<?> alias) {
        return new RaidHistory(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidHistory rename(String name) {
        return new RaidHistory(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidHistory rename(Name name) {
        return new RaidHistory(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidHistory rename(Table<?> name) {
        return new RaidHistory(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, Integer, String, String, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function5<? super String, ? super Integer, ? super String, ? super String, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function5<? super String, ? super Integer, ? super String, ? super String, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
