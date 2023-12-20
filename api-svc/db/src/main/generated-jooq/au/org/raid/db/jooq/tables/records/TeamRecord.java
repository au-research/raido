/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables.records;


import au.org.raid.db.jooq.tables.Team;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TeamRecord extends UpdatableRecordImpl<TeamRecord> implements Record4<String, String, String, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>api_svc.team.id</code>.
     */
    public TeamRecord setId(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.team.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>api_svc.team.name</code>.
     */
    public TeamRecord setName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.team.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>api_svc.team.prefix</code>.
     */
    public TeamRecord setPrefix(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.team.prefix</code>.
     */
    public String getPrefix() {
        return (String) get(2);
    }

    /**
     * Setter for <code>api_svc.team.service_point_id</code>.
     */
    public TeamRecord setServicePointId(Long value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.team.service_point_id</code>.
     */
    public Long getServicePointId() {
        return (Long) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, Long> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<String, String, String, Long> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Team.TEAM.ID;
    }

    @Override
    public Field<String> field2() {
        return Team.TEAM.NAME;
    }

    @Override
    public Field<String> field3() {
        return Team.TEAM.PREFIX;
    }

    @Override
    public Field<Long> field4() {
        return Team.TEAM.SERVICE_POINT_ID;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public String component3() {
        return getPrefix();
    }

    @Override
    public Long component4() {
        return getServicePointId();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public String value3() {
        return getPrefix();
    }

    @Override
    public Long value4() {
        return getServicePointId();
    }

    @Override
    public TeamRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public TeamRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public TeamRecord value3(String value) {
        setPrefix(value);
        return this;
    }

    @Override
    public TeamRecord value4(Long value) {
        setServicePointId(value);
        return this;
    }

    @Override
    public TeamRecord values(String value1, String value2, String value3, Long value4) {
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
     * Create a detached TeamRecord
     */
    public TeamRecord() {
        super(Team.TEAM);
    }

    /**
     * Create a detached, initialised TeamRecord
     */
    public TeamRecord(String id, String name, String prefix, Long servicePointId) {
        super(Team.TEAM);

        setId(id);
        setName(name);
        setPrefix(prefix);
        setServicePointId(servicePointId);
    }
}
