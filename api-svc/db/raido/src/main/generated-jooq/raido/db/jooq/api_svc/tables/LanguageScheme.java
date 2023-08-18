/*
 * This file is generated by jOOQ.
 */
package raido.db.jooq.api_svc.tables;


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

import raido.db.jooq.api_svc.ApiSvc;
import raido.db.jooq.api_svc.Keys;
import raido.db.jooq.api_svc.tables.records.LanguageSchemeRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LanguageScheme extends TableImpl<LanguageSchemeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc.language_scheme</code>
     */
    public static final LanguageScheme LANGUAGE_SCHEME = new LanguageScheme();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LanguageSchemeRecord> getRecordType() {
        return LanguageSchemeRecord.class;
    }

    /**
     * The column <code>api_svc.language_scheme.id</code>.
     */
    public final TableField<LanguageSchemeRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.language_scheme.uri</code>.
     */
    public final TableField<LanguageSchemeRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    private LanguageScheme(Name alias, Table<LanguageSchemeRecord> aliased) {
        this(alias, aliased, null);
    }

    private LanguageScheme(Name alias, Table<LanguageSchemeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.language_scheme</code> table reference
     */
    public LanguageScheme(String alias) {
        this(DSL.name(alias), LANGUAGE_SCHEME);
    }

    /**
     * Create an aliased <code>api_svc.language_scheme</code> table reference
     */
    public LanguageScheme(Name alias) {
        this(alias, LANGUAGE_SCHEME);
    }

    /**
     * Create a <code>api_svc.language_scheme</code> table reference
     */
    public LanguageScheme() {
        this(DSL.name("language_scheme"), null);
    }

    public <O extends Record> LanguageScheme(Table<O> child, ForeignKey<O, LanguageSchemeRecord> key) {
        super(child, key, LANGUAGE_SCHEME);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<LanguageSchemeRecord, Integer> getIdentity() {
        return (Identity<LanguageSchemeRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<LanguageSchemeRecord> getPrimaryKey() {
        return Keys.LANGUAGE_SCHEME_PKEY;
    }

    @Override
    public LanguageScheme as(String alias) {
        return new LanguageScheme(DSL.name(alias), this);
    }

    @Override
    public LanguageScheme as(Name alias) {
        return new LanguageScheme(alias, this);
    }

    @Override
    public LanguageScheme as(Table<?> alias) {
        return new LanguageScheme(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public LanguageScheme rename(String name) {
        return new LanguageScheme(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LanguageScheme rename(Name name) {
        return new LanguageScheme(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public LanguageScheme rename(Table<?> name) {
        return new LanguageScheme(name.getQualifiedName(), null);
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
