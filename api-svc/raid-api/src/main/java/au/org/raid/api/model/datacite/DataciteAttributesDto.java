package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DataciteAttributesDto {
    public String prefix;
    public String doi;
    public String publisher;
    public String publicationYear;
    private List<DataciteTitle> dataciteTitles;
    @JsonProperty("creators")
    public List<DataciteCreator> dataciteCreators;
    @JsonProperty("dates")
    public List<DataciteDate> dataciteDates;
    @JsonProperty("contributors")
    public List<DataciteContributor> dataciteContributors;
    @JsonProperty("descriptions")
    public List<DataciteDescription> dataciteDescriptions;
    @JsonProperty("relatedIdentifiers")
    public List<DataciteRelatedIdentifier> dataciteRelatedIdentifiers;
    @JsonProperty("rightsList")
    public List<DataciteRight> dataciteRights;
    @JsonProperty("types")
    public DataciteTypes dataciteTypes;
    @JsonProperty("alternateIdentifiers")
    public List<DataciteAlternateIdentifier> dataciteAlternateIdentifiers;

    public DataciteAttributesDto() {
        super();
    }

    public List<DataciteTitle> getTitles() {
        return dataciteTitles;
    }

    public DataciteAttributesDto setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
    public DataciteAttributesDto setDoi(String doi) {
        this.doi = doi;
        return this;
    }
    public DataciteAttributesDto setTitles(List<DataciteTitle> dataciteTitles) {
        this.dataciteTitles = dataciteTitles;
        return this;
    }

    public DataciteAttributesDto setCreators(List<DataciteCreator> dataciteCreators) {
        this.dataciteCreators = dataciteCreators;
        return this;
    }

    public DataciteAttributesDto setDates(List<DataciteDate> dataciteDates) {
        this.dataciteDates = dataciteDates;
        return this;
    }

    public DataciteAttributesDto setContributors(List<DataciteContributor> dataciteContributors) {
        this.dataciteContributors = dataciteContributors;
        return this;
    }

    public DataciteAttributesDto setDescriptions(List<DataciteDescription> dataciteDescriptions) {
        this.dataciteDescriptions = dataciteDescriptions;
        return this;
    }

    public DataciteAttributesDto setRelatedIdentifiers(List<DataciteRelatedIdentifier> dataciteRelatedIdentifiers) {
        this.dataciteRelatedIdentifiers = dataciteRelatedIdentifiers;
        return this;
    }

    public DataciteAttributesDto setRightsList(List<DataciteRight> dataciteRights) {
        this.dataciteRights = dataciteRights;
        return this;
    }

    public DataciteAttributesDto setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public DataciteAttributesDto setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
        return this;
    }

    public DataciteAttributesDto setDataciteTypes (DataciteTypes dataciteTypes) {
        this.dataciteTypes = dataciteTypes;
        return this;
    }

    public DataciteAttributesDto setDataciteAlternateIdentifiers (List<DataciteAlternateIdentifier> dataciteAlternateIdentifiers) {
        this.dataciteAlternateIdentifiers = dataciteAlternateIdentifiers;
        return this;
    }



}
