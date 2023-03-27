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
class RaidSchemaV1ValidationServiceTest {
  @Mock
  private SubjectValidationService subjectValidationService;
  @Mock
  private TitleValidationService titleValidationService;
  @Mock
  private DescriptionValidationService descSvc;
  @Mock
  private ContributorValidationService contribSvc;
  @Mock
  private OrganisationValidationService orgSvc;
  @Mock
  private IdentifierParser identifierParser;
  @Mock
  private RelatedRaidValidationService relatedRaidValidationService;

  @Mock
  private RelatedObjectValidationService relatedObjectValidationService;

  @Mock
  private AlternateIdentifierValidationService alternateIdentifierValidationService;

  @InjectMocks
  private RaidSchemaV1ValidationService validationService;

  @Test
  void validatesSubjectsOnCreate() {
    final var subjects = Collections.singletonList(new SubjectBlock());
    final var raid = new CreateRaidV1Request().subjects(subjects);

    validationService.validateForCreate(raid);

    verify(subjectValidationService).validateSubjects(subjects);
  }

  @Test
  void validatesSubjectsOnUpdate() {
    final var handle = "test-handle";
    final var subjects = Collections.singletonList(new SubjectBlock());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .subjects(subjects);

    validationService.validateForUpdate(handle, raid);

    verify(subjectValidationService).validateSubjects(subjects);
  }

  @Test
  void validatesRelatedRaidsOnCreate() {
    final var relatedRaids = Collections.singletonList(new RelatedRaidBlock());
    final var raid = new CreateRaidV1Request().relatedRaids(relatedRaids);

    validationService.validateForCreate(raid);
    verify(relatedRaidValidationService).validateRelatedRaids(relatedRaids);
  }

  @Test
  void validatesRelatedObjectsOnCreate() {
    final var relatedObjects = Collections.singletonList(new RelatedObjectBlock());
    final var raid = new CreateRaidV1Request().relatedObjects(relatedObjects);

    validationService.validateForCreate(raid);
    verify(relatedObjectValidationService).validateRelatedObjects(relatedObjects);
  }

  @Test
  void validatesRelatedRaidsOnUpdate() {
    final var handle = "test-handle";
    final var relatedRaids = Collections.singletonList(new RelatedRaidBlock());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .relatedRaids(relatedRaids);

    validationService.validateForUpdate(handle, raid);
    verify(relatedRaidValidationService).validateRelatedRaids(relatedRaids);
  }

  @Test
  void validatesRelatedObjectsOnUpdate() {
    final var handle = "test-handle";
    final var relatedObjects = Collections.singletonList(new RelatedObjectBlock());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .relatedObjects(relatedObjects);

    validationService.validateForUpdate(handle, raid);
    verify(relatedObjectValidationService).validateRelatedObjects(relatedObjects);
  }

  @Test
  void validatesAlternateIdentifiersOnCreate() {
    final var handle = "test-handle";
    final var alternateIdentifiers = Collections.singletonList(new AlternateIdentifierBlock());

    final var raid = new CreateRaidV1Request()
      .id(new IdBlock())
      .alternateIdentifiers(alternateIdentifiers);

    validationService.validateForCreate(raid);
    verify(alternateIdentifierValidationService).validateAlternateIdentifiers(alternateIdentifiers);
  }

  @Test
  void validatesAlternateIdentifiersOnUpdate() {
    final var handle = "test-handle";
    final var alternateIdentifiers = Collections.singletonList(new AlternateIdentifierBlock());

    final var raid = new UpdateRaidV1Request()
      .id(new IdBlock())
      .alternateIdentifiers(alternateIdentifiers);

    validationService.validateForUpdate(handle, raid);
    verify(alternateIdentifierValidationService).validateAlternateIdentifiers(alternateIdentifiers);
  }
}