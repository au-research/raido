/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.RelatedObjectCategorySchemaRecord;

import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
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
public class RelatedObjectCategorySchema extends TableImpl<RelatedObjectCategorySchemaRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of
     * <code>api_svc.related_object_category_schema</code>
     */
    public static final RelatedObjectCategorySchema RELATED_OBJECT_CATEGORY_SCHEMA = new RelatedObjectCategorySchema();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RelatedObjectCategorySchemaRecord> getRecordType() {
        return RelatedObjectCategorySchemaRecord.class;
    }

    /**
     * The column <code>api_svc.related_object_category_schema.id</code>.
     */
    public final TableField<RelatedObjectCategorySchemaRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.related_object_category_schema.uri</code>.
     */
    public final TableField<RelatedObjectCategorySchemaRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    private RelatedObjectCategorySchema(Name alias, Table<RelatedObjectCategorySchemaRecord> aliased) {
        this(alias, aliased, null);
    }

    private RelatedObjectCategorySchema(Name alias, Table<RelatedObjectCategorySchemaRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.related_object_category_schema</code>
     * table reference
     */
    public RelatedObjectCategorySchema(String alias) {
        this(DSL.name(alias), RELATED_OBJECT_CATEGORY_SCHEMA);
    }

    /**
     * Create an aliased <code>api_svc.related_object_category_schema</code>
     * table reference
     */
    public RelatedObjectCategorySchema(Name alias) {
        this(alias, RELATED_OBJECT_CATEGORY_SCHEMA);
    }

    /**
     * Create a <code>api_svc.related_object_category_schema</code> table
     * reference
     */
    public RelatedObjectCategorySchema() {
        this(DSL.name("related_object_category_schema"), null);
    }

    public <O extends Record> RelatedObjectCategorySchema(Table<O> child, ForeignKey<O, RelatedObjectCategorySchemaRecord> key) {
        super(child, key, RELATED_OBJECT_CATEGORY_SCHEMA);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<RelatedObjectCategorySchemaRecord, Integer> getIdentity() {
        return (Identity<RelatedObjectCategorySchemaRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<RelatedObjectCategorySchemaRecord> getPrimaryKey() {
        return Keys.RELATED_OBJECT_CATEGORY_SCHEMA_PKEY;
    }

    @Override
    public RelatedObjectCategorySchema as(String alias) {
        return new RelatedObjectCategorySchema(DSL.name(alias), this);
    }

    @Override
    public RelatedObjectCategorySchema as(Name alias) {
        return new RelatedObjectCategorySchema(alias, this);
    }

    @Override
    public RelatedObjectCategorySchema as(Table<?> alias) {
        return new RelatedObjectCategorySchema(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RelatedObjectCategorySchema rename(String name) {
        return new RelatedObjectCategorySchema(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RelatedObjectCategorySchema rename(Name name) {
        return new RelatedObjectCategorySchema(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RelatedObjectCategorySchema rename(Table<?> name) {
        return new RelatedObjectCategorySchema(name.getQualifiedName(), null);
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