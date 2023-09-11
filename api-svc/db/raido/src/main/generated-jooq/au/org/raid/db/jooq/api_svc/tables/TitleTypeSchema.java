/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.api_svc.tables;


import au.org.raid.db.jooq.api_svc.ApiSvc;
import au.org.raid.db.jooq.api_svc.Keys;
import au.org.raid.db.jooq.api_svc.tables.records.TitleTypeSchemaRecord;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.function.Function;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TitleTypeSchema extends TableImpl<TitleTypeSchemaRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.title_type_schema</code>
     */
    public static final TitleTypeSchema TITLE_TYPE_SCHEMA = new TitleTypeSchema();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TitleTypeSchemaRecord> getRecordType() {
        return TitleTypeSchemaRecord.class;
    }

    /**
     * The column <code>api_svc.title_type_schema.id</code>.
     */
    public final TableField<TitleTypeSchemaRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.title_type_schema.uri</code>.
     */
    public final TableField<TitleTypeSchemaRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    private TitleTypeSchema(Name alias, Table<TitleTypeSchemaRecord> aliased) {
        this(alias, aliased, null);
    }

    private TitleTypeSchema(Name alias, Table<TitleTypeSchemaRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.title_type_schema</code> table reference
     */
    public TitleTypeSchema(String alias) {
        this(DSL.name(alias), TITLE_TYPE_SCHEMA);
    }

    /**
     * Create an aliased <code>api_svc.title_type_schema</code> table reference
     */
    public TitleTypeSchema(Name alias) {
        this(alias, TITLE_TYPE_SCHEMA);
    }

    /**
     * Create a <code>api_svc.title_type_schema</code> table reference
     */
    public TitleTypeSchema() {
        this(DSL.name("title_type_schema"), null);
    }

    public <O extends Record> TitleTypeSchema(Table<O> child, ForeignKey<O, TitleTypeSchemaRecord> key) {
        super(child, key, TITLE_TYPE_SCHEMA);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<TitleTypeSchemaRecord, Integer> getIdentity() {
        return (Identity<TitleTypeSchemaRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<TitleTypeSchemaRecord> getPrimaryKey() {
        return Keys.TITLE_TYPE_SCHEMA_PKEY;
    }

    @Override
    public TitleTypeSchema as(String alias) {
        return new TitleTypeSchema(DSL.name(alias), this);
    }

    @Override
    public TitleTypeSchema as(Name alias) {
        return new TitleTypeSchema(alias, this);
    }

    @Override
    public TitleTypeSchema as(Table<?> alias) {
        return new TitleTypeSchema(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TitleTypeSchema rename(String name) {
        return new TitleTypeSchema(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TitleTypeSchema rename(Name name) {
        return new TitleTypeSchema(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TitleTypeSchema rename(Table<?> name) {
        return new TitleTypeSchema(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super Integer, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super Integer, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}