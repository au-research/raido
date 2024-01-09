package au.org.raid.api.exception;

public class SpatialCoverageSchemaNotFoundException extends RuntimeException {
    public SpatialCoverageSchemaNotFoundException(final String uri) {
        super("Spatial coverage schema not found %s".formatted(uri));
    }
    public SpatialCoverageSchemaNotFoundException(final Integer id) {
        super("Spatial coverage schema not found with id %d".formatted(id));
    }
}
