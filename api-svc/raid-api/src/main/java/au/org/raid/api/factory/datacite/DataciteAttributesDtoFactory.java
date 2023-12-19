package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.*;
import au.org.raid.idl.raidv2.model.Date;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataciteAttributesDtoFactory {
    private final DataciteTitleFactory dataciteTitleFactory;
    private final DataciteCreatorFactory dataciteCreatorFactory;
    private final DataciteDateFactory dataciteDateFactory;
    private final DataciteContributorFactory dataciteContributorFactory;
    private final DataciteDescriptionFactory dataciteDescriptionFactory;
    private final DataciteRelatedIdentifierFactory dataciteRelatedIdentifierFactory;



    @SneakyThrows
    public DataciteAttributesDto create(RaidCreateRequest request, String handle) {
        if (request == null) {
            return null;
        }

        List<DataciteTitle> dataciteTitles = null;
        if (request.getTitle() != null) {
            dataciteTitles = request.getTitle().stream()
                    .map(dataciteTitleFactory::create)
                    .toList();
        }

        List<DataciteCreator> dataciteCreators = null;
        if (request.getIdentifier() != null) {
            dataciteCreators = new ArrayList<>();
            dataciteCreators.add(dataciteCreatorFactory.create(request.getIdentifier().getRegistrationAgency()));
        }

        List<DataciteDate> dataciteDates = null;
        if (request.getDate() != null) {
            Date raidDates = request.getDate();
            dataciteDates.add(dataciteDateFactory.create(raidDates));
        }

        List<DataciteContributor> dataciteContributors = null;
        if (request.getContributor() != null) {
            dataciteContributors = request.getContributor().stream()
                    .map(dataciteContributorFactory::create)
                    .toList();
        }
        if (request.getOrganisation() != null) {
            dataciteContributors = request.getContributor().stream()
                    .map(dataciteContributorFactory::create)
                    .toList();
        }

        List<DataciteDescription> dataciteDescriptions = null;
        if (request.getDescription() != null) {
            dataciteDescriptions = request.getDescription().stream()
                    .map(dataciteDescriptionFactory::create)
                    .toList();
        }

        List<DataciteRelatedIdentifier> dataciteRelatedIdentifiers = null;
        if (request.getRelatedObject() != null) {
            dataciteRelatedIdentifiers = request.getRelatedObject().stream()
                    .map(dataciteRelatedIdentifierFactory::create)
                    .toList();
        }

        String prefix = handle.split("/")[0];

        return new DataciteAttributesDto()
                .setPrefix(prefix)
                .setDoi(handle)
                .setTitles(dataciteTitles)
                .setCreators(dataciteCreators)
                .setDates(dataciteDates)
                .setContributors(dataciteContributors)
                .setDescriptions(dataciteDescriptions)
                .setRelatedIdentifiers(dataciteRelatedIdentifiers);
    }

    @SneakyThrows
    public DataciteAttributesDto create(RaidUpdateRequest request, String handle) {
        if (request == null) {
            return null;
        }

        List<DataciteTitle> dataciteTitles = null;
        if (request.getTitle() != null) {
            dataciteTitles = request.getTitle().stream()
                    .map(dataciteTitleFactory::create)
                    .toList();
        }

        List<DataciteCreator> dataciteCreators = null;
        if (request.getIdentifier() != null) {
            dataciteCreators = new ArrayList<>();
            dataciteCreators.add(dataciteCreatorFactory.create(request.getIdentifier().getRegistrationAgency()));
        }

        String prefix = handle.split("/")[0];
        String suffix = handle.split("/")[1];

        return new DataciteAttributesDto().setPrefix(prefix).setDoi(handle).setTitles(dataciteTitles).setCreators(dataciteCreators);
    }

}
