package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.idl.raidv2.model.*;

import java.util.Collections;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RaidoStableV1ValidationServiceTest {
  @Mock
  private StableSubjectValidationService subjectValidationService;
  @Mock
  private StableTitleValidationService titleValidationService;
  @Mock
  private StableDescriptionValidationService descSvc;
  @Mock
  private StableContributorValidationService contribSvc;
  @Mock
  private StableOrganisationValidationService orgSvc;
  @Mock
  private IdentifierParser identifierParser;
  @Mock
  private StableRelatedRaidValidationService relatedRaidValidationService;
  @Mock
  private StableRelatedObjectValidationService relatedObjectValidationService;
  @Mock
  private StableAlternateIdentifierValidationService alternateIdentifierValidationService;

  @Mock
  private StableSpatialCoverageValidationService spatialCoverageValidationService;
  @Mock
  private StableTraditionalKnowledgeLabelValidatorService traditionalKnowledgeLabelValidatorService;
  @Mock
  private StableAccessValidationService accessValidationService;
  @InjectMocks
  private RaidoStableV1ValidationService validationService;

  @Test
  void validatesAccessOnCreate() {
    final var access = new Access();
    final var raid = new CreateRaidV1Request().access(access);

    validationService.validateForCreate(raid);

    verify(accessValidationService).validate(access);
  }

  @Test
  void validatesAccessOnUpdate() {
    final var handle = "test-handle";
    final var access = new Access();

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .access(access);

    validationService.validateForUpdate(handle, raid);

    verify(accessValidationService).validate(access);
  }

  @Test
  void validatesSubjectsOnCreate() {
    final var subjects = Collections.singletonList(new Subject());
    final var raid = new CreateRaidV1Request().subjects(subjects);

    validationService.validateForCreate(raid);

    verify(subjectValidationService).validateSubjects(subjects);
  }

  @Test
  void validatesSubjectsOnUpdate() {
    final var handle = "test-handle";
    final var subjects = Collections.singletonList(new Subject());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .subjects(subjects);

    validationService.validateForUpdate(handle, raid);

    verify(subjectValidationService).validateSubjects(subjects);
  }

  @Test
  void validatesRelatedRaidsOnCreate() {
    final var relatedRaids = Collections.singletonList(new RelatedRaid());
    final var raid = new CreateRaidV1Request().relatedRaids(relatedRaids);

    validationService.validateForCreate(raid);
    verify(relatedRaidValidationService).validateRelatedRaids(relatedRaids);
  }

  @Test
  void validatesRelatedObjectsOnCreate() {
    final var relatedObjects = Collections.singletonList(new RelatedObject());
    final var raid = new CreateRaidV1Request().relatedObjects(relatedObjects);

    validationService.validateForCreate(raid);
    verify(relatedObjectValidationService).validateRelatedObjects(relatedObjects);
  }

  @Test
  void validatesRelatedRaidsOnUpdate() {
    final var handle = "test-handle";
    final var relatedRaids = Collections.singletonList(new RelatedRaid());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .relatedRaids(relatedRaids);

    validationService.validateForUpdate(handle, raid);
    verify(relatedRaidValidationService).validateRelatedRaids(relatedRaids);
  }

  @Test
  void validatesRelatedObjectsOnUpdate() {
    final var handle = "test-handle";
    final var relatedObjects = Collections.singletonList(new RelatedObject());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .relatedObjects(relatedObjects);

    validationService.validateForUpdate(handle, raid);
    verify(relatedObjectValidationService).validateRelatedObjects(relatedObjects);
  }

  @Test
  void validatesAlternateIdentifiersOnCreate() {
    final var handle = "test-handle";
    final var alternateIdentifiers = Collections.singletonList(new AlternateIdentifier());

    final var raid = new CreateRaidV1Request()
      .id(new IdBlock())
      .alternateIdentifiers(alternateIdentifiers);

    validationService.validateForCreate(raid);
    verify(alternateIdentifierValidationService).validateAlternateIdentifiers(alternateIdentifiers);
  }

  @Test
  void validatesAlternateIdentifiersOnUpdate() {
    final var handle = "test-handle";
    final var alternateIdentifiers = Collections.singletonList(new AlternateIdentifier());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .alternateIdentifiers(alternateIdentifiers);

    validationService.validateForUpdate(handle, raid);
    verify(alternateIdentifierValidationService).validateAlternateIdentifiers(alternateIdentifiers);
  }

  @Test
  void validatesSpatialCoverageOnCreate() {
    final var handle = "test-handle";
    final var spatialCoverages =
      Collections.singletonList(new SpatialCoverage());

    final var raid = new CreateRaidV1Request()
      .id(new IdBlock())
      .spatialCoverages(spatialCoverages);

    validationService.validateForCreate(raid);
    verify(spatialCoverageValidationService).validate(spatialCoverages);
  }

  @Test
  void validatesSpatialCoverageOnUpdate() {
    final var handle = "test-handle";
    final var spatialCoverages =
      Collections.singletonList(new SpatialCoverage());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .spatialCoverages(spatialCoverages);

    validationService.validateForUpdate(handle, raid);
    verify(spatialCoverageValidationService).validate(spatialCoverages);
  }

  @Test
  void validatesTraditionalKnowledgeLabelsOnCreate() {
    final var handle = "test-handle";
    final var traditionalKnowledgeLabels =
      Collections.singletonList(new TraditionalKnowledgeLabel ());

    final var raid = new CreateRaidV1Request()
      .id(new IdBlock())
      .traditionalKnowledgeLabels(traditionalKnowledgeLabels);

    validationService.validateForCreate(raid);
    verify(traditionalKnowledgeLabelValidatorService).validateTraditionalKnowledgeLabels(traditionalKnowledgeLabels);
  }

  @Test
  void validatesTraditionalKnowledgeLabelsOnUpdate() {
    final var handle = "test-handle";
    final var traditionalKnowledgeLabels =
      Collections.singletonList(new TraditionalKnowledgeLabel());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .traditionalKnowledgeLabels(traditionalKnowledgeLabels);

    validationService.validateForUpdate(handle, raid);
    verify(traditionalKnowledgeLabelValidatorService).validateTraditionalKnowledgeLabels(traditionalKnowledgeLabels);
  }
}