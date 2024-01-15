package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DataciteAttributesDto {
    private String prefix;
    private String doi;
    private String publisher;
    private String publicationYear;
    @JsonProperty("titles")
    private List<DataciteTitle> dataciteTitles;
    @JsonProperty("creators")
    private List<DataciteCreator> dataciteCreators;
    @JsonProperty("dates")
    private List<DataciteDate> dataciteDates;
    @JsonProperty("contributors")
    private List<DataciteContributor> dataciteContributors;
    @JsonProperty("descriptions")
    private List<DataciteDescription> dataciteDescriptions;
    @JsonProperty("relatedIdentifiers")
    private List<DataciteRelatedIdentifier> dataciteRelatedIdentifiers;
    @JsonProperty("rightsList")
    private List<DataciteRight> dataciteRights;
    @JsonProperty("types")
    private DataciteTypes dataciteTypes;
    @JsonProperty("alternateIdentifiers")
    private List<DataciteAlternateIdentifier> dataciteAlternateIdentifiers;
}
