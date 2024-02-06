/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.RelatedObjectSchemaRecord;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RelatedObjectSchema extends TableImpl<RelatedObjectSchemaRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.related_object_schema</code>
     */
    public static final RelatedObjectSchema RELATED_OBJECT_SCHEMA = new RelatedObjectSchema();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RelatedObjectSchemaRecord> getRecordType() {
        return RelatedObjectSchemaRecord.class;
    }

    /**
     * The column <code>api_svc.related_object_schema.id</code>.
     */
    public final TableField<RelatedObjectSchemaRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.related_object_schema.uri</code>.
     */
    public final TableField<RelatedObjectSchemaRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    private RelatedObjectSchema(Name alias, Table<RelatedObjectSchemaRecord> aliased) {
        this(alias, aliased, null);
    }

    private RelatedObjectSchema(Name alias, Table<RelatedObjectSchemaRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.related_object_schema</code> table
     * reference
     */
    public RelatedObjectSchema(String alias) {
        this(DSL.name(alias), RELATED_OBJECT_SCHEMA);
    }

    /**
     * Create an aliased <code>api_svc.related_object_schema</code> table
     * reference
     */
    public RelatedObjectSchema(Name alias) {
        this(alias, RELATED_OBJECT_SCHEMA);
    }

    /**
     * Create a <code>api_svc.related_object_schema</code> table reference
     */
    public RelatedObjectSchema() {
        this(DSL.name("related_object_schema"), null);
    }

    public <O extends Record> RelatedObjectSchema(Table<O> child, ForeignKey<O, RelatedObjectSchemaRecord> key) {
        super(child, key, RELATED_OBJECT_SCHEMA);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<RelatedObjectSchemaRecord, Integer> getIdentity() {
        return (Identity<RelatedObjectSchemaRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<RelatedObjectSchemaRecord> getPrimaryKey() {
        return Keys.RELATED_OBJECT_SCHEMA_PKEY;
    }

    @Override
    public List<UniqueKey<RelatedObjectSchemaRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.RELATED_OBJECT_SCHEMA_URI_KEY);
    }

    @Override
    public RelatedObjectSchema as(String alias) {
        return new RelatedObjectSchema(DSL.name(alias), this);
    }

    @Override
    public RelatedObjectSchema as(Name alias) {
        return new RelatedObjectSchema(alias, this);
    }

    @Override
    public RelatedObjectSchema as(Table<?> alias) {
        return new RelatedObjectSchema(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RelatedObjectSchema rename(String name) {
        return new RelatedObjectSchema(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RelatedObjectSchema rename(Name name) {
        return new RelatedObjectSchema(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RelatedObjectSchema rename(Table<?> name) {
        return new RelatedObjectSchema(name.getQualifiedName(), null);
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
