/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables.records;


import au.org.raid.db.jooq.tables.RaidAlternateUrl;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaidAlternateUrlRecord extends UpdatableRecordImpl<RaidAlternateUrlRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>api_svc.raid_alternate_url.handle</code>.
     */
    public RaidAlternateUrlRecord setHandle(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid_alternate_url.handle</code>.
     */
    public String getHandle() {
        return (String) get(0);
    }

    /**
     * Setter for <code>api_svc.raid_alternate_url.url</code>.
     */
    public RaidAlternateUrlRecord setUrl(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raid_alternate_url.url</code>.
     */
    public String getUrl() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<String, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return RaidAlternateUrl.RAID_ALTERNATE_URL.HANDLE;
    }

    @Override
    public Field<String> field2() {
        return RaidAlternateUrl.RAID_ALTERNATE_URL.URL;
    }

    @Override
    public String component1() {
        return getHandle();
    }

    @Override
    public String component2() {
        return getUrl();
    }

    @Override
    public String value1() {
        return getHandle();
    }

    @Override
    public String value2() {
        return getUrl();
    }

    @Override
    public RaidAlternateUrlRecord value1(String value) {
        setHandle(value);
        return this;
    }

    @Override
    public RaidAlternateUrlRecord value2(String value) {
        setUrl(value);
        return this;
    }

    @Override
    public RaidAlternateUrlRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RaidAlternateUrlRecord
     */
    public RaidAlternateUrlRecord() {
        super(RaidAlternateUrl.RAID_ALTERNATE_URL);
    }

    /**
     * Create a detached, initialised RaidAlternateUrlRecord
     */
    public RaidAlternateUrlRecord(String handle, String url) {
        super(RaidAlternateUrl.RAID_ALTERNATE_URL);

        setHandle(handle);
        setUrl(url);
    }
}
