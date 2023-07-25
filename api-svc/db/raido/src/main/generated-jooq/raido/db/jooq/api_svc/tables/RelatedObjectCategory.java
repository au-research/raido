/*
 * This file is generated by jOOQ.
 */
package raido.db.jooq.api_svc.tables;


import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import raido.db.jooq.api_svc.ApiSvc;
import raido.db.jooq.api_svc.Keys;
import raido.db.jooq.api_svc.tables.records.RelatedObjectCategoryRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


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
     * The column <code>api_svc.related_object_category.scheme_id</code>.
     */
    public final TableField<RelatedObjectCategoryRecord, Integer> SCHEME_ID = createField(DSL.name("scheme_id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>api_svc.related_object_category.uri</code>.
     */
    public final TableField<RelatedObjectCategoryRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

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
    public UniqueKey<RelatedObjectCategoryRecord> getPrimaryKey() {
        return Keys.RELATED_OBJECT_CATEGORY_PKEY;
    }

    @Override
    public List<ForeignKey<RelatedObjectCategoryRecord, ?>> getReferences() {
        return Arrays.asList(Keys.RELATED_OBJECT_CATEGORY__FK_RELATED_OBJECT_CATEGORY_TYPE_SCHEME_ID);
    }

    private transient RelatedObjectCategoryScheme _relatedObjectCategoryScheme;

    /**
     * Get the implicit join path to the
     * <code>api_svc.related_object_category_scheme</code> table.
     */
    public RelatedObjectCategoryScheme relatedObjectCategoryScheme() {
        if (_relatedObjectCategoryScheme == null)
            _relatedObjectCategoryScheme = new RelatedObjectCategoryScheme(this, Keys.RELATED_OBJECT_CATEGORY__FK_RELATED_OBJECT_CATEGORY_TYPE_SCHEME_ID);

        return _relatedObjectCategoryScheme;
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