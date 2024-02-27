package au.org.raid.api.model.datacite;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public class DataciteDto {
    public String schemaVersion;
    public String type;
    @Getter
    private DataciteAttributesDto attributes;

    private final List<DataciteIdentifier> dataciteIdentifiers = new ArrayList<>();

    public DataciteDto() {
        super();
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
