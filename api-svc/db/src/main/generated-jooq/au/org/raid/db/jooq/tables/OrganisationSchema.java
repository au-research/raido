/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.OrganisationSchemaRecord;

import java.util.Arrays;
import java.util.List;
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
public class OrganisationSchema extends TableImpl<OrganisationSchemaRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.organisation_schema</code>
     */
    public static final OrganisationSchema ORGANISATION_SCHEMA = new OrganisationSchema();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrganisationSchemaRecord> getRecordType() {
        return OrganisationSchemaRecord.class;
    }

    /**
     * The column <code>api_svc.organisation_schema.id</code>.
     */
    public final TableField<OrganisationSchemaRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.organisation_schema.uri</code>.
     */
    public final TableField<OrganisationSchemaRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    private OrganisationSchema(Name alias, Table<OrganisationSchemaRecord> aliased) {
        this(alias, aliased, null);
    }

    private OrganisationSchema(Name alias, Table<OrganisationSchemaRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.organisation_schema</code> table
     * reference
     */
    public OrganisationSchema(String alias) {
        this(DSL.name(alias), ORGANISATION_SCHEMA);
    }

    /**
     * Create an aliased <code>api_svc.organisation_schema</code> table
     * reference
     */
    public OrganisationSchema(Name alias) {
        this(alias, ORGANISATION_SCHEMA);
    }

    /**
     * Create a <code>api_svc.organisation_schema</code> table reference
     */
    public OrganisationSchema() {
        this(DSL.name("organisation_schema"), null);
    }

    public <O extends Record> OrganisationSchema(Table<O> child, ForeignKey<O, OrganisationSchemaRecord> key) {
        super(child, key, ORGANISATION_SCHEMA);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<OrganisationSchemaRecord, Integer> getIdentity() {
        return (Identity<OrganisationSchemaRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<OrganisationSchemaRecord> getPrimaryKey() {
        return Keys.ORGANISATION_SCHEMA_PKEY;
    }

    @Override
    public List<UniqueKey<OrganisationSchemaRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.ORGANISATION_SCHEMA_URI_KEY);
    }

    @Override
    public OrganisationSchema as(String alias) {
        return new OrganisationSchema(DSL.name(alias), this);
    }

    @Override
    public OrganisationSchema as(Name alias) {
        return new OrganisationSchema(alias, this);
    }

    @Override
    public OrganisationSchema as(Table<?> alias) {
        return new OrganisationSchema(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public OrganisationSchema rename(String name) {
        return new OrganisationSchema(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public OrganisationSchema rename(Name name) {
        return new OrganisationSchema(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public OrganisationSchema rename(Table<?> name) {
        return new OrganisationSchema(name.getQualifiedName(), null);
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