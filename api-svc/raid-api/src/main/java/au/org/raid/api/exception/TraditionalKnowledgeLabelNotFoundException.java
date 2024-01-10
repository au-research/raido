package au.org.raid.api.exception;

public class TraditionalKnowledgeLabelNotFoundException extends RuntimeException {
    public TraditionalKnowledgeLabelNotFoundException(final String id) {
        super("Traditional knowledge label not found with id %s".formatted(id));
    }
    public TraditionalKnowledgeLabelNotFoundException(final Integer id) {
        super("Traditional knowledge label not found with id %s".formatted(id));
    }
}
