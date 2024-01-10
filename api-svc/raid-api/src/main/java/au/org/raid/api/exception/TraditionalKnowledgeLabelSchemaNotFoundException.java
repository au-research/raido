package au.org.raid.api.exception;

public class TraditionalKnowledgeLabelSchemaNotFoundException extends RuntimeException {
    public TraditionalKnowledgeLabelSchemaNotFoundException(final String schemaUri) {
        super("Traditional knowledge label schema not found %s".formatted(schemaUri));
    }

    public TraditionalKnowledgeLabelSchemaNotFoundException(final Integer id) {
        super("Traditional knowledge label schema not found with id %d".formatted(id));
    }
}
