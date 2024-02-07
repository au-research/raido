/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.ContributorSchemaRecord;

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
public class ContributorSchema extends TableImpl<ContributorSchemaRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.contributor_schema</code>
     */
    public static final ContributorSchema CONTRIBUTOR_SCHEMA = new ContributorSchema();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContributorSchemaRecord> getRecordType() {
        return ContributorSchemaRecord.class;
    }

    /**
     * The column <code>api_svc.contributor_schema.id</code>.
     */
    public final TableField<ContributorSchemaRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.contributor_schema.uri</code>.
     */
    public final TableField<ContributorSchemaRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    private ContributorSchema(Name alias, Table<ContributorSchemaRecord> aliased) {
        this(alias, aliased, null);
    }

    private ContributorSchema(Name alias, Table<ContributorSchemaRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.contributor_schema</code> table reference
     */
    public ContributorSchema(String alias) {
        this(DSL.name(alias), CONTRIBUTOR_SCHEMA);
    }

    /**
     * Create an aliased <code>api_svc.contributor_schema</code> table reference
     */
    public ContributorSchema(Name alias) {
        this(alias, CONTRIBUTOR_SCHEMA);
    }

    /**
     * Create a <code>api_svc.contributor_schema</code> table reference
     */
    public ContributorSchema() {
        this(DSL.name("contributor_schema"), null);
    }

    public <O extends Record> ContributorSchema(Table<O> child, ForeignKey<O, ContributorSchemaRecord> key) {
        super(child, key, CONTRIBUTOR_SCHEMA);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<ContributorSchemaRecord, Integer> getIdentity() {
        return (Identity<ContributorSchemaRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<ContributorSchemaRecord> getPrimaryKey() {
        return Keys.CONTRIBUTOR_SCHEMA_PKEY;
    }

    @Override
    public ContributorSchema as(String alias) {
        return new ContributorSchema(DSL.name(alias), this);
    }

    @Override
    public ContributorSchema as(Name alias) {
        return new ContributorSchema(alias, this);
    }

    @Override
    public ContributorSchema as(Table<?> alias) {
        return new ContributorSchema(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public ContributorSchema rename(String name) {
        return new ContributorSchema(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContributorSchema rename(Name name) {
        return new ContributorSchema(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContributorSchema rename(Table<?> name) {
        return new ContributorSchema(name.getQualifiedName(), null);
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
