package au.org.raid.api.model.datacite;

import java.util.ArrayList;
import java.util.List;


public class DataciteDto {
    public String schemaVersion;
    public String type;
    private DataciteAttributesDto attributes;

    private List<DataciteIdentifier> dataciteIdentifiers = new ArrayList<>();

    public DataciteDto() {
        super();
    }

    public DataciteAttributesDto getAttributes() {
        return attributes;
    }

    public DataciteDto setAttributes(DataciteAttributesDto dataciteAttributes) {
        this.attributes = dataciteAttributes;
        return this;
    }

    public DataciteDto setIdentifier(DataciteIdentifier dataciteIdentifier) {
        this.dataciteIdentifiers.add(dataciteIdentifier);
        return this;
    }

    public DataciteDto setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
        return this;
    }
    public DataciteDto setType(String type) {
        this.type = type;
        return this;
    }

}
