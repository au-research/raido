package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.idl.raidv2.model.CreateRaidV1Request;
import raido.idl.raidv2.model.IdBlock;
import raido.idl.raidv2.model.SubjectBlock;
import raido.idl.raidv2.model.UpdateRaidV1Request;

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
}