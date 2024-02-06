/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables.records;


import au.org.raid.db.jooq.tables.RaidoOperator;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Row1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * any app_user with an email in this table will be considered an operator
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaidoOperatorRecord extends UpdatableRecordImpl<RaidoOperatorRecord> implements Record1<String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>api_svc.raido_operator.email</code>.
     */
    public RaidoOperatorRecord setEmail(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>api_svc.raido_operator.email</code>.
     */
    public String getEmail() {
        return (String) get(0);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record1 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row1<String> fieldsRow() {
        return (Row1) super.fieldsRow();
    }

    @Override
    public Row1<String> valuesRow() {
        return (Row1) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return RaidoOperator.RAIDO_OPERATOR.EMAIL;
    }

    @Override
    public String component1() {
        return getEmail();
    }

    @Override
    public String value1() {
        return getEmail();
    }

    @Override
    public RaidoOperatorRecord value1(String value) {
        setEmail(value);
        return this;
    }

    @Override
    public RaidoOperatorRecord values(String value1) {
        value1(value1);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RaidoOperatorRecord
     */
    public RaidoOperatorRecord() {
        super(RaidoOperator.RAIDO_OPERATOR);
    }

    /**
     * Create a detached, initialised RaidoOperatorRecord
     */
    public RaidoOperatorRecord(String email) {
        super(RaidoOperator.RAIDO_OPERATOR);

        setEmail(email);
    }
}
