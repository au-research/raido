package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.SubjectRepository;
import raido.db.jooq.api_svc.tables.records.SubjectRecord;
import raido.idl.raidv2.model.SubjectBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectValidationServiceTest {

  @Mock
  private SubjectRepository subjectRepository;

  @InjectMocks
  private SubjectValidationService validationService;

  @Test
  void noFailuresWithValidCode() {
    final var id = "https://linked.data.gov.au/def/anzsrc-for/2020/222222";

    final var subject = new SubjectBlock().id(id);

    when(subjectRepository.findById("222222")).thenReturn(Optional.of(new SubjectRecord()));

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures, empty());
  }

  @Test
  void noFailuresIfSubjectBlockIsNull() {
    final List<ValidationFailure> validationFailures = validationService.validateSubjects(null);
    assertThat(validationFailures, empty());
  }

  @Test
  void returnsFailureWithAlphabeticCharactersInId() {
    final var id = "https://linked.data.gov.au/def/anzsrc-for/2020/22a222";

    final var subject = new SubjectBlock().id(id);

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getMessage(), is(String.format("%s is not a valid field of research", id)));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getFieldId(), is("subjects[0].id"));

    verifyNoInteractions(subjectRepository);
  }

  @Test
  void returnsFailureWithInvalidUrlPrefix() {
    final var id = "https://data.gov.au/def/anzsrc-for/2020/222222";

    final var subject = new SubjectBlock().id(id);

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getMessage(), is(String.format("%s is not a valid field of research", id)));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getFieldId(), is("subjects[0].id"));

    verifyNoInteractions(subjectRepository);
  }

  @Test
  void returnsFailureIfCodeNotFound() {
    final var id = "https://linked.data.gov.au/def/anzsrc-for/2020/222222";

    final var subject = new SubjectBlock().id(id);

    when(subjectRepository.findById("222222")).thenReturn(Optional.empty());

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getMessage(), is(String.format("%s is not a valid field of research", id)));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getFieldId(), is("subjects[0].id"));  }
}