/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.RelatedObjectCategoryRecord;

import java.util.Arrays;
import java.util.List;
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
public class RelatedObjectCategory extends TableImpl<RelatedObjectCategoryRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.related_object_category</code>
     */
    public static final RelatedObjectCategory RELATED_OBJECT_CATEGORY = new RelatedObjectCategory();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RelatedObjectCategoryRecord> getRecordType() {
        return RelatedObjectCategoryRecord.class;
    }

    /**
     * The column <code>api_svc.related_object_category.id</code>.
     */
    public final TableField<RelatedObjectCategoryRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.related_object_category.uri</code>.
     */
    public final TableField<RelatedObjectCategoryRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>api_svc.related_object_category.schema_id</code>.
     */
    public final TableField<RelatedObjectCategoryRecord, Integer> SCHEMA_ID = createField(DSL.name("schema_id"), SQLDataType.INTEGER.nullable(false), this, "");

    private RelatedObjectCategory(Name alias, Table<RelatedObjectCategoryRecord> aliased) {
        this(alias, aliased, null);
    }

    private RelatedObjectCategory(Name alias, Table<RelatedObjectCategoryRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.related_object_category</code> table
     * reference
     */
    public RelatedObjectCategory(String alias) {
        this(DSL.name(alias), RELATED_OBJECT_CATEGORY);
    }

    /**
     * Create an aliased <code>api_svc.related_object_category</code> table
     * reference
     */
    public RelatedObjectCategory(Name alias) {
        this(alias, RELATED_OBJECT_CATEGORY);
    }

    /**
     * Create a <code>api_svc.related_object_category</code> table reference
     */
    public RelatedObjectCategory() {
        this(DSL.name("related_object_category"), null);
    }

    public <O extends Record> RelatedObjectCategory(Table<O> child, ForeignKey<O, RelatedObjectCategoryRecord> key) {
        super(child, key, RELATED_OBJECT_CATEGORY);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<RelatedObjectCategoryRecord, Integer> getIdentity() {
        return (Identity<RelatedObjectCategoryRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<RelatedObjectCategoryRecord> getPrimaryKey() {
        return Keys.RELATED_OBJECT_CATEGORY_NEW_PKEY;
    }

    @Override
    public List<UniqueKey<RelatedObjectCategoryRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.RELATED_OBJECT_CATEGORY_NEW_URI_SCHEMA_ID_KEY);
    }

    @Override
    public RelatedObjectCategory as(String alias) {
        return new RelatedObjectCategory(DSL.name(alias), this);
    }

    @Override
    public RelatedObjectCategory as(Name alias) {
        return new RelatedObjectCategory(alias, this);
    }

    @Override
    public RelatedObjectCategory as(Table<?> alias) {
        return new RelatedObjectCategory(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RelatedObjectCategory rename(String name) {
        return new RelatedObjectCategory(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RelatedObjectCategory rename(Name name) {
        return new RelatedObjectCategory(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RelatedObjectCategory rename(Table<?> name) {
        return new RelatedObjectCategory(name.getQualifiedName(), null);
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
