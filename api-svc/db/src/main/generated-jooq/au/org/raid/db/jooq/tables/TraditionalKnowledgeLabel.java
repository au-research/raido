/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq.tables;


import au.org.raid.db.jooq.ApiSvc;
import au.org.raid.db.jooq.Keys;
import au.org.raid.db.jooq.tables.records.TraditionalKnowledgeLabelRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TraditionalKnowledgeLabel extends TableImpl<TraditionalKnowledgeLabelRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of
     * <code>api_svc.traditional_knowledge_label</code>
     */
    public static final TraditionalKnowledgeLabel TRADITIONAL_KNOWLEDGE_LABEL = new TraditionalKnowledgeLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TraditionalKnowledgeLabelRecord> getRecordType() {
        return TraditionalKnowledgeLabelRecord.class;
    }

    /**
     * The column <code>api_svc.traditional_knowledge_label.id</code>.
     */
    public final TableField<TraditionalKnowledgeLabelRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>api_svc.traditional_knowledge_label.uri</code>.
     */
    public final TableField<TraditionalKnowledgeLabelRecord, String> URI = createField(DSL.name("uri"), SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>api_svc.traditional_knowledge_label.schema_id</code>.
     */
    public final TableField<TraditionalKnowledgeLabelRecord, Integer> SCHEMA_ID = createField(DSL.name("schema_id"), SQLDataType.INTEGER.nullable(false), this, "");

    private TraditionalKnowledgeLabel(Name alias, Table<TraditionalKnowledgeLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private TraditionalKnowledgeLabel(Name alias, Table<TraditionalKnowledgeLabelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>api_svc.traditional_knowledge_label</code> table
     * reference
     */
    public TraditionalKnowledgeLabel(String alias) {
        this(DSL.name(alias), TRADITIONAL_KNOWLEDGE_LABEL);
    }

    /**
     * Create an aliased <code>api_svc.traditional_knowledge_label</code> table
     * reference
     */
    public TraditionalKnowledgeLabel(Name alias) {
        this(alias, TRADITIONAL_KNOWLEDGE_LABEL);
    }

    /**
     * Create a <code>api_svc.traditional_knowledge_label</code> table reference
     */
    public TraditionalKnowledgeLabel() {
        this(DSL.name("traditional_knowledge_label"), null);
    }

    public <O extends Record> TraditionalKnowledgeLabel(Table<O> child, ForeignKey<O, TraditionalKnowledgeLabelRecord> key) {
        super(child, key, TRADITIONAL_KNOWLEDGE_LABEL);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : ApiSvc.API_SVC;
    }

    @Override
    public Identity<TraditionalKnowledgeLabelRecord, Integer> getIdentity() {
        return (Identity<TraditionalKnowledgeLabelRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<TraditionalKnowledgeLabelRecord> getPrimaryKey() {
        return Keys.TRADITIONAL_KNOWLEDGE_LABEL_NEW_PKEY;
    }

    @Override
    public List<UniqueKey<TraditionalKnowledgeLabelRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.TRADITIONAL_KNOWLEDGE_LABEL_NEW_URI_SCHEMA_ID_KEY);
    }

    @Override
    public TraditionalKnowledgeLabel as(String alias) {
        return new TraditionalKnowledgeLabel(DSL.name(alias), this);
    }

    @Override
    public TraditionalKnowledgeLabel as(Name alias) {
        return new TraditionalKnowledgeLabel(alias, this);
    }

    @Override
    public TraditionalKnowledgeLabel as(Table<?> alias) {
        return new TraditionalKnowledgeLabel(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TraditionalKnowledgeLabel rename(String name) {
        return new TraditionalKnowledgeLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TraditionalKnowledgeLabel rename(Name name) {
        return new TraditionalKnowledgeLabel(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TraditionalKnowledgeLabel rename(Table<?> name) {
        return new TraditionalKnowledgeLabel(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super Integer, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super Integer, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}