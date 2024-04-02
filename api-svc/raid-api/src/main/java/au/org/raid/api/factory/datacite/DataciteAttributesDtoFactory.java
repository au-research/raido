package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.*;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.idl.raidv2.model.Access;
import au.org.raid.idl.raidv2.model.Id;
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
    private final DataciteRightFactory dataciteRightFactory;
    private final DataciteTypesFactory dataciteTypesFactory;
    private final DataciteAlternateIdentifierFactory dataciteAlternateIdentifierFactory;
    private final DatacitePublisherFactory datacitePublisherFactory;

    @SneakyThrows
    public DataciteAttributesDto create(RaidCreateRequest request, String handle) {
        final var contributors = new ArrayList<DataciteContributor>();

        contributors.add(dataciteContributorFactory.create(
                request.getIdentifier().getRegistrationAgency()
        ));

        if (request.getOrganisation() != null) {
            contributors.addAll(request.getOrganisation().stream()
                    .map(dataciteContributorFactory::create)
                    .toList());
        }

        final var publisher = datacitePublisherFactory.create(request.getIdentifier().getOwner());

        List<DataciteTitle> dataciteTitles = new ArrayList<>();

        final var primaryTitle = request.getTitle().stream()
                .filter(t -> t.getType().getId().equals(SchemaValues.PRIMARY_TITLE_TYPE.getUri()))
                .findFirst()
                .orElseThrow();

        dataciteTitles.add(dataciteTitleFactory.create(primaryTitle));
//        request.getTitle().remove(primaryTitle);

        if (request.getTitle() != null) {
            dataciteTitles = request.getTitle().stream()
                    .map(dataciteTitleFactory::create)
                    .toList();
        }

        List<DataciteCreator> dataciteCreators = new ArrayList<>();
//        if (request.getIdentifier() != null) {
//            dataciteCreators = new ArrayList<>();
//            dataciteCreators.add(dataciteCreatorFactory.create(request.getIdentifier().getRegistrationAgency()));
//        }

        List<DataciteDate> dataciteDates = List.of(dataciteDateFactory.create(request.getDate()));

        List<DataciteDescription> dataciteDescriptions = new ArrayList<>();

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
//            DataciteRight dataciteRight = dataciteRightFactory.create(access, raidId);
//            dataciteRights.add(dataciteRight);
        }

        List<DataciteAlternateIdentifier> dataciteAlternateIdentifiers = new ArrayList<>();
        if (request.getAlternateIdentifier() != null) {
            dataciteAlternateIdentifiers = request.getAlternateIdentifier().stream()
                    .map(dataciteAlternateIdentifierFactory::create)
                    .toList();
        }

        DataciteTypes dataciteTypes = dataciteTypesFactory.create("RAiD Project", "Other");


        String prefix = handle.split("/")[0];

        return new DataciteAttributesDto()
                .setPrefix(prefix)
                .setDoi(handle)
                .setPublisher(publisher) // $.identifier.registrationAgency
                .setPublicationYear(String.valueOf(java.time.Year.now())) // TODO: year of start date
                .setTypes(dataciteTypes)
                .setTitles(dataciteTitles)
                .setCreators(dataciteCreators)
                .setDates(dataciteDates)
                .setContributors(contributors)
                .setDescriptions(dataciteDescriptions)
                .setRelatedIdentifiers(dataciteRelatedIdentifiers)
                .setRightsList(dataciteRights)
                .setAlternateIdentifiers(dataciteAlternateIdentifiers);
    }

    @SneakyThrows
    public DataciteAttributesDto create(RaidUpdateRequest request, String handle) {
        return null;
//
//        List<DataciteContributor> contributors = new ArrayList<>();
//
//        contributors.add(dataciteContributorFactory.create(
//                request.getIdentifier().getRegistrationAgency()
//        ));
//
//        contributors.addAll(request.getOrganisation().stream()
//                .map(dataciteContributorFactory::create)
//                .toList());
//
//
//
//
//        List<DataciteTitle> dataciteTitles = new ArrayList<>();
//        if (request.getTitle() != null) {
//            dataciteTitles = request.getTitle().stream()
//                    .map(dataciteTitleFactory::create)
//                    .toList();
//        }
//
//        List<DataciteCreator> dataciteCreators =
//                request.getContributor().stream()
//                        .map(dataciteCreatorFactory::create)
//                        .toList();
//
//
//        List<DataciteDate> dataciteDates = new ArrayList<>();
//        if (request.getDate() != null) {
//            Date raidDates = request.getDate();
//            dataciteDates.add(dataciteDateFactory.create(raidDates));
//        }
//
//
//
//        List<DataciteDescription> dataciteDescriptions = new ArrayList<>();
//        if (request.getDescription() != null) {
//            dataciteDescriptions = request.getDescription().stream()
//                    .map(dataciteDescriptionFactory::create)
//                    .toList();
//        }
//
//        List<DataciteRelatedIdentifier> dataciteRelatedIdentifiers = new ArrayList<>();
//
//        if (request.getRelatedObject() != null) {
//            dataciteRelatedIdentifiers.addAll(request.getRelatedObject().stream()
//                    .map(dataciteRelatedIdentifierFactory::create)
//                    .toList());
//        }
//
//        if (request.getAlternateUrl() != null) {
//            dataciteRelatedIdentifiers.addAll(request.getAlternateUrl().stream()
//                    .map(dataciteRelatedIdentifierFactory::create)
//                    .toList());
//        }
//
//        if (request.getRelatedRaid() != null) {
//            dataciteRelatedIdentifiers.addAll(request.getRelatedRaid().stream()
//                    .map(dataciteRelatedIdentifierFactory::create)
//                    .toList());
//        }
//
//        List<DataciteRight> dataciteRights = new ArrayList<>();
//        if (request.getIdentifier() != null) {
//            Id raidId = request.getIdentifier();
//            Access access = request.getAccess();
//            DataciteRight dataciteRight = dataciteRightFactory.create(access, raidId);
//            dataciteRights.add(dataciteRight);
//        }
//
//        List<DataciteAlternateIdentifier> dataciteAlternateIdentifiers = new ArrayList<>();
//        if (request.getAlternateIdentifier() != null) {
//            dataciteAlternateIdentifiers = request.getAlternateIdentifier().stream()
//                    .map(dataciteAlternateIdentifierFactory::create)
//                    .toList();
//        }
//
//        DataciteTypes dataciteTypes = dataciteTypesFactory.create("RAiD Project", "Other");
//
//        String prefix = handle.split("/")[0];
//
//        return new DataciteAttributesDto()
//                .setPrefix(prefix)
//                .setDoi(handle)
////                .setPublisher("ARDC")
//                .setPublicationYear(String.valueOf(java.time.Year.now()))
//                .setTypes(dataciteTypes)
//                .setTitles(dataciteTitles)
//                .setCreators(dataciteCreators)
//                .setDates(dataciteDates)
//                .setContributors(contributors)
//                .setDescriptions(dataciteDescriptions)
//                .setRelatedIdentifiers(dataciteRelatedIdentifiers)
//                .setRightsList(dataciteRights)
//                .setAlternateIdentifiers(dataciteAlternateIdentifiers);
    }

}
