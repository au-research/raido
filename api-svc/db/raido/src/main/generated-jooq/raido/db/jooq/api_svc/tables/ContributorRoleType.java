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
import raido.db.jooq.api_svc.tables.records.ContributorRoleTypeRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ContributorRoleType extends TableImpl<ContributorRoleTypeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.contributor_role_type</code>
     */
    public static final ContributorRoleType CONTRIBUTOR_ROLE_TYPE = new ContributorRoleType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContributorRoleTypeRecord> getRecordType() {
        return ContributorRoleTypeRecord.class;
    }

    /**
     * The column <code>api_svc.contributor_role_type.scheme_id</code>.
     */
    public final TableField<ContributorRoleTypeRecord, Integer> SCHEME_ID = createField(DSL.name("scheme_id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>api_svc.contributor_role_type.uri</code>.
     */
    public final TableField<ContributorRoleTypeRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    private ContributorRoleType(Name alias, Table<ContributorRoleTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private ContributorRoleType(Name alias, Table<ContributorRoleTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.contributor_role_type</code> table
     * reference
     */
    public ContributorRoleType(String alias) {
        this(DSL.name(alias), CONTRIBUTOR_ROLE_TYPE);
    }

    /**
     * Create an aliased <code>api_svc.contributor_role_type</code> table
     * reference
     */
    public ContributorRoleType(Name alias) {
        this(alias, CONTRIBUTOR_ROLE_TYPE);
    }

    /**
     * Create a <code>api_svc.contributor_role_type</code> table reference
     */
    public ContributorRoleType() {
        this(DSL.name("contributor_role_type"), null);
    }

    public <O extends Record> ContributorRoleType(Table<O> child, ForeignKey<O, ContributorRoleTypeRecord> key) {
        super(child, key, CONTRIBUTOR_ROLE_TYPE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public UniqueKey<ContributorRoleTypeRecord> getPrimaryKey() {
        return Keys.CONTRIBUTOR_ROLE_TYPE_PKEY;
    }

    @Override
    public List<ForeignKey<ContributorRoleTypeRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CONTRIBUTOR_ROLE_TYPE__FK_ACCESS_TYPE_SCHEME_ID);
    }

    private transient AccessTypeScheme _accessTypeScheme;

    /**
     * Get the implicit join path to the <code>api_svc.access_type_scheme</code>
     * table.
     */
    public AccessTypeScheme accessTypeScheme() {
        if (_accessTypeScheme == null)
            _accessTypeScheme = new AccessTypeScheme(this, Keys.CONTRIBUTOR_ROLE_TYPE__FK_ACCESS_TYPE_SCHEME_ID);

        return _accessTypeScheme;
    }

    @Override
    public ContributorRoleType as(String alias) {
        return new ContributorRoleType(DSL.name(alias), this);
    }

    @Override
    public ContributorRoleType as(Name alias) {
        return new ContributorRoleType(alias, this);
    }

    @Override
    public ContributorRoleType as(Table<?> alias) {
        return new ContributorRoleType(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public ContributorRoleType rename(String name) {
        return new ContributorRoleType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContributorRoleType rename(Name name) {
        return new ContributorRoleType(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContributorRoleType rename(Table<?> name) {
        return new ContributorRoleType(name.getQualifiedName(), null);
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
