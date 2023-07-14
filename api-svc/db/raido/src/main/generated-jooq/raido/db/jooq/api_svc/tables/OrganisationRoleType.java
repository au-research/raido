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
import raido.db.jooq.api_svc.tables.records.OrganisationRoleTypeRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OrganisationRoleType extends TableImpl<OrganisationRoleTypeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.organisation_role_type</code>
     */
    public static final OrganisationRoleType ORGANISATION_ROLE_TYPE = new OrganisationRoleType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrganisationRoleTypeRecord> getRecordType() {
        return OrganisationRoleTypeRecord.class;
    }

    /**
     * The column <code>api_svc.organisation_role_type.scheme_id</code>.
     */
    public final TableField<OrganisationRoleTypeRecord, Integer> SCHEME_ID = createField(DSL.name("scheme_id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>api_svc.organisation_role_type.uri</code>.
     */
    public final TableField<OrganisationRoleTypeRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    private OrganisationRoleType(Name alias, Table<OrganisationRoleTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private OrganisationRoleType(Name alias, Table<OrganisationRoleTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.organisation_role_type</code> table
     * reference
     */
    public OrganisationRoleType(String alias) {
        this(DSL.name(alias), ORGANISATION_ROLE_TYPE);
    }

    /**
     * Create an aliased <code>api_svc.organisation_role_type</code> table
     * reference
     */
    public OrganisationRoleType(Name alias) {
        this(alias, ORGANISATION_ROLE_TYPE);
    }

    /**
     * Create a <code>api_svc.organisation_role_type</code> table reference
     */
    public OrganisationRoleType() {
        this(DSL.name("organisation_role_type"), null);
    }

    public <O extends Record> OrganisationRoleType(Table<O> child, ForeignKey<O, OrganisationRoleTypeRecord> key) {
        super(child, key, ORGANISATION_ROLE_TYPE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public UniqueKey<OrganisationRoleTypeRecord> getPrimaryKey() {
        return Keys.ORGANISATION_ROLE_TYPE_PKEY;
    }

    @Override
    public List<ForeignKey<OrganisationRoleTypeRecord, ?>> getReferences() {
        return Arrays.asList(Keys.ORGANISATION_ROLE_TYPE__FK_ORGANISATION_ROLE_TYPE_SCHEME_ID);
    }

    private transient OrganisationRoleTypeScheme _organisationRoleTypeScheme;

    /**
     * Get the implicit join path to the
     * <code>api_svc.organisation_role_type_scheme</code> table.
     */
    public OrganisationRoleTypeScheme organisationRoleTypeScheme() {
        if (_organisationRoleTypeScheme == null)
            _organisationRoleTypeScheme = new OrganisationRoleTypeScheme(this, Keys.ORGANISATION_ROLE_TYPE__FK_ORGANISATION_ROLE_TYPE_SCHEME_ID);

        return _organisationRoleTypeScheme;
    }

    @Override
    public OrganisationRoleType as(String alias) {
        return new OrganisationRoleType(DSL.name(alias), this);
    }

    @Override
    public OrganisationRoleType as(Name alias) {
        return new OrganisationRoleType(alias, this);
    }

    @Override
    public OrganisationRoleType as(Table<?> alias) {
        return new OrganisationRoleType(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public OrganisationRoleType rename(String name) {
        return new OrganisationRoleType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public OrganisationRoleType rename(Name name) {
        return new OrganisationRoleType(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public OrganisationRoleType rename(Table<?> name) {
        return new OrganisationRoleType(name.getQualifiedName(), null);
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
