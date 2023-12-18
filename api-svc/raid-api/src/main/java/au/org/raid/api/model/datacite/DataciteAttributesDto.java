package au.org.raid.api.model.datacite;

import java.util.List;

public class DataciteAttributesDto {
    public String prefix;
    public String doi;
    private List<DataciteTitle> dataciteTitles;
    public List<DataciteCreator> dataciteCreators;
    public List<DataciteDate> dataciteDates;


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

}
