/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.ContributorPositionSchemaRecord;

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
public class ContributorPositionSchema extends TableImpl<ContributorPositionSchemaRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of
     * <code>api_svc.contributor_position_schema</code>
     */
    public static final ContributorPositionSchema CONTRIBUTOR_POSITION_SCHEMA = new ContributorPositionSchema();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContributorPositionSchemaRecord> getRecordType() {
        return ContributorPositionSchemaRecord.class;
    }

    /**
     * The column <code>api_svc.contributor_position_schema.id</code>.
     */
    public final TableField<ContributorPositionSchemaRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.contributor_position_schema.uri</code>.
     */
    public final TableField<ContributorPositionSchemaRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    private ContributorPositionSchema(Name alias, Table<ContributorPositionSchemaRecord> aliased) {
        this(alias, aliased, null);
    }

    private ContributorPositionSchema(Name alias, Table<ContributorPositionSchemaRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.contributor_position_schema</code> table
     * reference
     */
    public ContributorPositionSchema(String alias) {
        this(DSL.name(alias), CONTRIBUTOR_POSITION_SCHEMA);
    }

    /**
     * Create an aliased <code>api_svc.contributor_position_schema</code> table
     * reference
     */
    public ContributorPositionSchema(Name alias) {
        this(alias, CONTRIBUTOR_POSITION_SCHEMA);
    }

    /**
     * Create a <code>api_svc.contributor_position_schema</code> table reference
     */
    public ContributorPositionSchema() {
        this(DSL.name("contributor_position_schema"), null);
    }

    public <O extends Record> ContributorPositionSchema(Table<O> child, ForeignKey<O, ContributorPositionSchemaRecord> key) {
        super(child, key, CONTRIBUTOR_POSITION_SCHEMA);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<ContributorPositionSchemaRecord, Integer> getIdentity() {
        return (Identity<ContributorPositionSchemaRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<ContributorPositionSchemaRecord> getPrimaryKey() {
        return Keys.CONTRIBUTOR_POSITION_SCHEMA_PKEY;
    }

    @Override
    public ContributorPositionSchema as(String alias) {
        return new ContributorPositionSchema(DSL.name(alias), this);
    }

    @Override
    public ContributorPositionSchema as(Name alias) {
        return new ContributorPositionSchema(alias, this);
    }

    @Override
    public ContributorPositionSchema as(Table<?> alias) {
        return new ContributorPositionSchema(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public ContributorPositionSchema rename(String name) {
        return new ContributorPositionSchema(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContributorPositionSchema rename(Name name) {
        return new ContributorPositionSchema(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContributorPositionSchema rename(Table<?> name) {
        return new ContributorPositionSchema(name.getQualifiedName(), null);
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
