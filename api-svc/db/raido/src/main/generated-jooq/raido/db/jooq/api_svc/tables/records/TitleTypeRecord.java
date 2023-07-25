/*
 * This file is generated by jOOQ.
 */
package raido.db.jooq.api_svc.tables.records;


import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;
import raido.db.jooq.api_svc.tables.TitleType;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TitleTypeRecord extends UpdatableRecordImpl<TitleTypeRecord> implements Record2<Integer, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>api_svc.title_type.scheme_id</code>.
     */
    public TitleTypeRecord setSchemeId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.title_type.scheme_id</code>.
     */
    public Integer getSchemeId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>api_svc.title_type.uri</code>.
     */
    public TitleTypeRecord setUri(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.title_type.uri</code>.
     */
    public String getUri() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<Integer, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Integer, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return TitleType.TITLE_TYPE.SCHEME_ID;
    }

    @Override
    public Field<String> field2() {
        return TitleType.TITLE_TYPE.URI;
    }

    @Override
    public Integer component1() {
        return getSchemeId();
    }

    @Override
    public String component2() {
        return getUri();
    }

    @Override
    public Integer value1() {
        return getSchemeId();
    }

    @Override
    public String value2() {
        return getUri();
    }

    @Override
    public TitleTypeRecord value1(Integer value) {
        setSchemeId(value);
        return this;
    }

    @Override
    public TitleTypeRecord value2(String value) {
        setUri(value);
        return this;
    }

    @Override
    public TitleTypeRecord values(Integer value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TitleTypeRecord
     */
    public TitleTypeRecord() {
        super(TitleType.TITLE_TYPE);
    }

    /**
     * Create a detached, initialised TitleTypeRecord
     */
    public TitleTypeRecord(Integer schemeId, String uri) {
        super(TitleType.TITLE_TYPE);

        setSchemeId(schemeId);
        setUri(uri);
    }
}