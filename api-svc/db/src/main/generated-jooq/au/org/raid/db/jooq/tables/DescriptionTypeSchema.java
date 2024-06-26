/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.enums.SchemaStatus;
import au.org.raid.db.jooq.tables.records.DescriptionTypeSchemaRecord;

import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Identity;
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
public class DescriptionTypeSchema extends TableImpl<DescriptionTypeSchemaRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.description_type_schema</code>
     */
    public static final DescriptionTypeSchema DESCRIPTION_TYPE_SCHEMA = new DescriptionTypeSchema();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DescriptionTypeSchemaRecord> getRecordType() {
        return DescriptionTypeSchemaRecord.class;
    }

    /**
     * The column <code>api_svc.description_type_schema.id</code>.
     */
    public final TableField<DescriptionTypeSchemaRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.description_type_schema.uri</code>.
     */
    public final TableField<DescriptionTypeSchemaRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>api_svc.description_type_schema.status</code>.
     */
    public final TableField<DescriptionTypeSchemaRecord, SchemaStatus> STATUS = createField(DSL.name("status"), SQLDataType.VARCHAR.asEnumDataType(au.org.raid.db.jooq.enums.SchemaStatus.class), this, "");

    private DescriptionTypeSchema(Name alias, Table<DescriptionTypeSchemaRecord> aliased) {
        this(alias, aliased, null);
    }

    private DescriptionTypeSchema(Name alias, Table<DescriptionTypeSchemaRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.description_type_schema</code> table
     * reference
     */
    public DescriptionTypeSchema(String alias) {
        this(DSL.name(alias), DESCRIPTION_TYPE_SCHEMA);
    }

    /**
     * Create an aliased <code>api_svc.description_type_schema</code> table
     * reference
     */
    public DescriptionTypeSchema(Name alias) {
        this(alias, DESCRIPTION_TYPE_SCHEMA);
    }

    /**
     * Create a <code>api_svc.description_type_schema</code> table reference
     */
    public DescriptionTypeSchema() {
        this(DSL.name("description_type_schema"), null);
    }

    public <O extends Record> DescriptionTypeSchema(Table<O> child, ForeignKey<O, DescriptionTypeSchemaRecord> key) {
        super(child, key, DESCRIPTION_TYPE_SCHEMA);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<DescriptionTypeSchemaRecord, Integer> getIdentity() {
        return (Identity<DescriptionTypeSchemaRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<DescriptionTypeSchemaRecord> getPrimaryKey() {
        return Keys.DWSCRIPTION_TYPE_SCHEMA_PKEY;
    }

    @Override
    public DescriptionTypeSchema as(String alias) {
        return new DescriptionTypeSchema(DSL.name(alias), this);
    }

    @Override
    public DescriptionTypeSchema as(Name alias) {
        return new DescriptionTypeSchema(alias, this);
    }

    @Override
    public DescriptionTypeSchema as(Table<?> alias) {
        return new DescriptionTypeSchema(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public DescriptionTypeSchema rename(String name) {
        return new DescriptionTypeSchema(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DescriptionTypeSchema rename(Name name) {
        return new DescriptionTypeSchema(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public DescriptionTypeSchema rename(Table<?> name) {
        return new DescriptionTypeSchema(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, SchemaStatus> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super Integer, ? super String, ? super SchemaStatus, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super Integer, ? super String, ? super SchemaStatus, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
