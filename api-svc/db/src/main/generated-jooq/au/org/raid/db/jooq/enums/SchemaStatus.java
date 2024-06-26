/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.enums;


import au.org.raid.db.jooq.ApiSvc;

import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public enum SchemaStatus implements EnumType {

    active("active"),

    inactive("inactive");

    private final String literal;

    private SchemaStatus(String literal) {
        this.literal = literal;
    }

    @Override
    public Catalog getCatalog() {
        return getSchema().getCatalog();
    }

    @Override
    public Schema getSchema() {
        return ApiSvc.API_SVC;
    }

    @Override
    public String getName() {
        return "schema_status";
    }

    @Override
    public String getLiteral() {
        return literal;
    }

    /**
     * Lookup a value of this EnumType by its literal
     */
    public static SchemaStatus lookupLiteral(String literal) {
        return EnumType.lookupLiteral(SchemaStatus.class, literal);
    }
}
