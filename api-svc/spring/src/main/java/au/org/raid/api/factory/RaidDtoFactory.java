package au.org.raid.api.factory;

import au.org.raid.db.jooq.api_svc.enums.Metaschema;
import au.org.raid.db.jooq.api_svc.tables.records.RaidRecord;
import au.org.raid.idl.raidv2.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RaidDtoFactory {
    private final ObjectMapper objectMapper;
    private final IdFactory idFactory;
    private final TitleFactory titleFactory;
    private final DescriptionFactory descriptionFactory;
    private final DatesFactory datesFactory;
    private final ContributorFactory contributorFactory;
    private final OrganisationFactory organisationFactory;
    private final RelatedObjectFactory relatedObjectFactory;
    private final AlternateIdentifierFactory alternateIdentifierFactory;
    private final AlternateUrlFactory alternateUrlFactory;
    private final RelatedRaidFactory relatedRaidFactory;
    private final SubjectFactory subjectFactory;
    private final TraditionalKnowledgeLabelFactory traditionalKnowledgeLabelFactory;
    private final SpatialCoverageFactory spatialCoverageFactory;
    private final AccessFactory accessFactory;

    @SneakyThrows
    public RaidDto create(RaidRecord raidRecord) {
        if (raidRecord == null) {
            return null;
        }
        if (raidRecord.getMetadataSchema() == Metaschema.raido_metadata_schema_v2) {
            return objectMapper.readValue(raidRecord.getMetadata().data(), RaidDto.class);
        } else {
            if (raidRecord.getMetadata() == null) {
                return null;
            }

            final var metadata =
                    objectMapper.readValue(raidRecord.getMetadata().data(), RaidoMetadataSchemaV1.class);

            List<Title> titles = null;
            if (metadata.getTitles() != null) {
                titles = metadata.getTitles().stream()
                        .map(titleFactory::create)
                        .toList();
            }

            List<Description> descriptions = null;
            if (metadata.getDescriptions() != null) {
                descriptions = metadata.getDescriptions().stream()
                        .map(descriptionFactory::create)
                        .toList();
            }

            List<Contributor> contributors = null;
            if (metadata.getContributors() != null) {
                contributors = metadata.getContributors().stream()
                        .map(contributorFactory::create)
                        .toList();
            }

            List<Organisation> organisations = null;
            if (metadata.getOrganisations() != null) {
                organisations = metadata.getOrganisations().stream()
                        .map(organisationFactory::create)
                        .toList();
            }

            List<RelatedObject> relatedObjects = null;
            if (metadata.getRelatedObjects() != null) {
                relatedObjects = metadata.getRelatedObjects().stream()
                        .map(relatedObjectFactory::create)
                        .toList();
            }

            List<AlternateIdentifier> alternateIdentifiers = null;
            if (metadata.getAlternateIdentifiers() != null) {
                alternateIdentifiers = metadata.getAlternateIdentifiers().stream()
                        .map(alternateIdentifierFactory::create)
                        .toList();
            }

            List<AlternateUrl> alternateUrls = null;
            if (metadata.getAlternateUrls() != null) {
                alternateUrls = metadata.getAlternateUrls().stream()
                        .map(alternateUrlFactory::create)
                        .toList();
            }

            List<RelatedRaid> relatedRaids = null;
            if (metadata.getRelatedRaids() != null) {
                relatedRaids = metadata.getRelatedRaids().stream()
                        .map(relatedRaidFactory::create)
                        .toList();
            }

            List<Subject> subjects = null;
            if (metadata.getSubjects() != null) {
                subjects = metadata.getSubjects().stream()
                        .map(subjectFactory::create)
                        .toList();
            }

            List<TraditionalKnowledgeLabel> traditionalKnowledgeLabels = null;
            if (metadata.getTraditionalKnowledgeLabels() != null) {
                traditionalKnowledgeLabels = metadata.getTraditionalKnowledgeLabels().stream()
                        .map(traditionalKnowledgeLabelFactory::create)
                        .toList();
            }

            List<SpatialCoverage> spatialCoverages = null;
            if (metadata.getSpatialCoverages() != null)
                spatialCoverages = metadata.getSpatialCoverages().stream()
                        .map(spatialCoverageFactory::create)
                        .toList();

            return new RaidDto()
                    .id(idFactory.create(metadata.getId()))
                    .dates(datesFactory.create(metadata.getDates()))
                    .titles(titles)
                    .descriptions(descriptions)
                    .contributors(contributors)
                    .organisations(organisations)
                    .relatedObjects(relatedObjects)
                    .alternateIdentifiers(alternateIdentifiers)
                    .alternateUrls(alternateUrls)
                    .relatedRaids(relatedRaids)
                    .subjects(subjects)
                    .traditionalKnowledgeLabels(traditionalKnowledgeLabels)
                    .spatialCoverages(spatialCoverages)
                    .access(accessFactory.create(metadata.getAccess()));
        }
    }
}