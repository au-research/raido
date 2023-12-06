/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.RaidRelatedObjectCategoryRecord;

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
public class RaidRelatedObjectCategory extends TableImpl<RaidRelatedObjectCategoryRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of
     * <code>api_svc.raid_related_object_category</code>
     */
    public static final RaidRelatedObjectCategory RAID_RELATED_OBJECT_CATEGORY = new RaidRelatedObjectCategory();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RaidRelatedObjectCategoryRecord> getRecordType() {
        return RaidRelatedObjectCategoryRecord.class;
    }

    /**
     * The column <code>api_svc.raid_related_object_category.id</code>.
     */
    public final TableField<RaidRelatedObjectCategoryRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column
     * <code>api_svc.raid_related_object_category.raid_related_object_id</code>.
     */
    public final TableField<RaidRelatedObjectCategoryRecord, Integer> RAID_RELATED_OBJECT_ID = createField(DSL.name("raid_related_object_id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column
     * <code>api_svc.raid_related_object_category.related_object_category_id</code>.
     */
    public final TableField<RaidRelatedObjectCategoryRecord, Integer> RELATED_OBJECT_CATEGORY_ID = createField(DSL.name("related_object_category_id"), SQLDataType.INTEGER.nullable(false), this, "");

    private RaidRelatedObjectCategory(Name alias, Table<RaidRelatedObjectCategoryRecord> aliased) {
        this(alias, aliased, null);
    }

    private RaidRelatedObjectCategory(Name alias, Table<RaidRelatedObjectCategoryRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.raid_related_object_category</code> table
     * reference
     */
    public RaidRelatedObjectCategory(String alias) {
        this(DSL.name(alias), RAID_RELATED_OBJECT_CATEGORY);
    }

    /**
     * Create an aliased <code>api_svc.raid_related_object_category</code> table
     * reference
     */
    public RaidRelatedObjectCategory(Name alias) {
        this(alias, RAID_RELATED_OBJECT_CATEGORY);
    }

    /**
     * Create a <code>api_svc.raid_related_object_category</code> table
     * reference
     */
    public RaidRelatedObjectCategory() {
        this(DSL.name("raid_related_object_category"), null);
    }

    public <O extends Record> RaidRelatedObjectCategory(Table<O> child, ForeignKey<O, RaidRelatedObjectCategoryRecord> key) {
        super(child, key, RAID_RELATED_OBJECT_CATEGORY);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<RaidRelatedObjectCategoryRecord, Integer> getIdentity() {
        return (Identity<RaidRelatedObjectCategoryRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<RaidRelatedObjectCategoryRecord> getPrimaryKey() {
        return Keys.RAID_RELATED_OBJECT_CATEGORY_PKEY;
    }

    @Override
    public List<UniqueKey<RaidRelatedObjectCategoryRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.RAID_RELATED_OBJECT_CATEGORY_RAID_RELATED_OBJECT_ID_RELATED_KEY);
    }

    @Override
    public List<ForeignKey<RaidRelatedObjectCategoryRecord, ?>> getReferences() {
        return Arrays.asList(Keys.RAID_RELATED_OBJECT_CATEGORY__FK_RAID_RELATED_OBJECT_CATEGORY_RAID_RELATED_OBJECT_ID, Keys.RAID_RELATED_OBJECT_CATEGORY__FK_RAID_RELATED_OBJECT_CATEGORY_RELATED_OBJECT_CATEGORY_ID);
    }

    private transient RaidRelatedObject _raidRelatedObject;
    private transient RelatedObjectCategory _relatedObjectCategory;

    /**
     * Get the implicit join path to the
     * <code>api_svc.raid_related_object</code> table.
     */
    public RaidRelatedObject raidRelatedObject() {
        if (_raidRelatedObject == null)
            _raidRelatedObject = new RaidRelatedObject(this, Keys.RAID_RELATED_OBJECT_CATEGORY__FK_RAID_RELATED_OBJECT_CATEGORY_RAID_RELATED_OBJECT_ID);

        return _raidRelatedObject;
    }

    /**
     * Get the implicit join path to the
     * <code>api_svc.related_object_category</code> table.
     */
    public RelatedObjectCategory relatedObjectCategory() {
        if (_relatedObjectCategory == null)
            _relatedObjectCategory = new RelatedObjectCategory(this, Keys.RAID_RELATED_OBJECT_CATEGORY__FK_RAID_RELATED_OBJECT_CATEGORY_RELATED_OBJECT_CATEGORY_ID);

        return _relatedObjectCategory;
    }

    @Override
    public RaidRelatedObjectCategory as(String alias) {
        return new RaidRelatedObjectCategory(DSL.name(alias), this);
    }

    @Override
    public RaidRelatedObjectCategory as(Name alias) {
        return new RaidRelatedObjectCategory(alias, this);
    }

    @Override
    public RaidRelatedObjectCategory as(Table<?> alias) {
        return new RaidRelatedObjectCategory(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidRelatedObjectCategory rename(String name) {
        return new RaidRelatedObjectCategory(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidRelatedObjectCategory rename(Name name) {
        return new RaidRelatedObjectCategory(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RaidRelatedObjectCategory rename(Table<?> name) {
        return new RaidRelatedObjectCategory(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, Integer, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super Integer, ? super Integer, ? super Integer, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super Integer, ? super Integer, ? super Integer, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
