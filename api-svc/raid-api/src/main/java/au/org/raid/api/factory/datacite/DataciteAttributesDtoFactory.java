package au.org.raid.api.factory.datacite;

import au.org.raid.api.factory.DescriptionFactory;
import au.org.raid.api.model.datacite.*;
import au.org.raid.idl.raidv2.model.*;
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
    private final DataciteRightFactory dataciteRightFactory;
    private final DataciteTypesFactory dataciteTypesFactory;

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

        List<DataciteDate> dataciteDates = new ArrayList<>();
        if (request.getDate() != null) {
            Date raidDates = request.getDate();
            dataciteDates.add(dataciteDateFactory.create(raidDates));
        }

        List<DataciteContributor> dataciteContributors = new ArrayList<>();
        if (request.getContributor() != null) {
            dataciteContributors.addAll(request.getContributor().stream()
                    .map(dataciteContributorFactory::create)
                    .toList());
        }
        if (request.getOrganisation() != null) {
            dataciteContributors.addAll(request.getOrganisation().stream()
                    .map(dataciteContributorFactory::create)
                    .toList());
        }

        List<DataciteDescription> dataciteDescriptions = null;
        if (request.getDescription() != null) {
            dataciteDescriptions = request.getDescription().stream()
                    .map(dataciteDescriptionFactory::create)
                    .toList();
        }

        List<DataciteRelatedIdentifier> dataciteRelatedIdentifiers = new ArrayList<>();

        if (request.getRelatedObject() != null) {
            dataciteRelatedIdentifiers.addAll(request.getRelatedObject().stream()
                    .map(dataciteRelatedIdentifierFactory::create)
                    .toList());
        }

        if (request.getAlternateUrl() != null) {
            dataciteRelatedIdentifiers.addAll(request.getAlternateUrl().stream()
                    .map(dataciteRelatedIdentifierFactory::create)
                    .toList());
        }

        if (request.getRelatedRaid() != null) {
            dataciteRelatedIdentifiers.addAll(request.getRelatedRaid().stream()
                    .map(dataciteRelatedIdentifierFactory::create)
                    .toList());
        }

        List<DataciteRight> dataciteRights = new ArrayList<>();
        if (request.getIdentifier() != null) {
            Id raidId = request.getIdentifier();
            Access access = request.getAccess();
            DataciteRight dataciteRight = dataciteRightFactory.create(access, raidId);
            dataciteRights.add(dataciteRight);
        }

        DataciteTypes dataciteTypes = dataciteTypesFactory.create("RAiD Project", "Other");


        String prefix = handle.split("/")[0];

        return new DataciteAttributesDto()
                .setPrefix(prefix)
                .setDoi(handle)
                .setPublisher("ARDC")
                .setPublicationYear(String.valueOf(java.time.Year.now()))
                .setDataciteTypes(dataciteTypes)
                .setTitles(dataciteTitles)
                .setCreators(dataciteCreators)
                .setDates(dataciteDates)
                .setContributors(dataciteContributors)
                .setDescriptions(dataciteDescriptions)
                .setRelatedIdentifiers(dataciteRelatedIdentifiers)
                .setRightsList(dataciteRights);
    }

}
