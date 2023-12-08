/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables.records;


import au.org.raid.db.jooq.tables.DescriptionType;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DescriptionTypeRecord extends UpdatableRecordImpl<DescriptionTypeRecord> implements Record3<Integer, String, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>api_svc.description_type.id</code>.
     */
    public DescriptionTypeRecord setId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.description_type.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>api_svc.description_type.uri</code>.
     */
    public DescriptionTypeRecord setUri(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.description_type.uri</code>.
     */
    public String getUri() {
        return (String) get(1);
    }

    /**
     * Setter for <code>api_svc.description_type.schema_id</code>.
     */
    public DescriptionTypeRecord setSchemaId(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.description_type.schema_id</code>.
     */
    public Integer getSchemaId() {
        return (Integer) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, String, Integer> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return DescriptionType.DESCRIPTION_TYPE.ID;
    }

    @Override
    public Field<String> field2() {
        return DescriptionType.DESCRIPTION_TYPE.URI;
    }

    @Override
    public Field<Integer> field3() {
        return DescriptionType.DESCRIPTION_TYPE.SCHEMA_ID;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getUri();
    }

    @Override
    public Integer component3() {
        return getSchemaId();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getUri();
    }

    @Override
    public Integer value3() {
        return getSchemaId();
    }

    @Override
    public DescriptionTypeRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public DescriptionTypeRecord value2(String value) {
        setUri(value);
        return this;
    }

    @Override
    public DescriptionTypeRecord value3(Integer value) {
        setSchemaId(value);
        return this;
    }

    @Override
    public DescriptionTypeRecord values(Integer value1, String value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DescriptionTypeRecord
     */
    public DescriptionTypeRecord() {
        super(DescriptionType.DESCRIPTION_TYPE);
    }

    /**
     * Create a detached, initialised DescriptionTypeRecord
     */
    public DescriptionTypeRecord(Integer id, String uri, Integer schemaId) {
        super(DescriptionType.DESCRIPTION_TYPE);

        setId(id);
        setUri(uri);
        setSchemaId(schemaId);
    }
}