/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables.records;


import au.org.raid.db.jooq.tables.Language;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LanguageRecord extends UpdatableRecordImpl<LanguageRecord> implements Record4<Integer, String, String, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>api_svc.language.id</code>.
     */
    public LanguageRecord setId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.language.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>api_svc.language.code</code>.
     */
    public LanguageRecord setCode(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.language.code</code>.
     */
    public String getCode() {
        return (String) get(1);
    }

    /**
     * Setter for <code>api_svc.language.name</code>.
     */
    public LanguageRecord setName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.language.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>api_svc.language.schema_id</code>.
     */
    public LanguageRecord setSchemaId(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.language.schema_id</code>.
     */
    public Integer getSchemaId() {
        return (Integer) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, String, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Integer, String, String, Integer> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Language.LANGUAGE.ID;
    }

    @Override
    public Field<String> field2() {
        return Language.LANGUAGE.CODE;
    }

    @Override
    public Field<String> field3() {
        return Language.LANGUAGE.NAME;
    }

    @Override
    public Field<Integer> field4() {
        return Language.LANGUAGE.SCHEMA_ID;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getCode();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public Integer component4() {
        return getSchemaId();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getCode();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public Integer value4() {
        return getSchemaId();
    }

    @Override
    public LanguageRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public LanguageRecord value2(String value) {
        setCode(value);
        return this;
    }

    @Override
    public LanguageRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public LanguageRecord value4(Integer value) {
        setSchemaId(value);
        return this;
    }

    @Override
    public LanguageRecord values(Integer value1, String value2, String value3, Integer value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LanguageRecord
     */
    public LanguageRecord() {
        super(Language.LANGUAGE);
    }

    /**
     * Create a detached, initialised LanguageRecord
     */
    public LanguageRecord(Integer id, String code, String name, Integer schemaId) {
        super(Language.LANGUAGE);

        setId(id);
        setCode(code);
        setName(name);
        setSchemaId(schemaId);
    }
}