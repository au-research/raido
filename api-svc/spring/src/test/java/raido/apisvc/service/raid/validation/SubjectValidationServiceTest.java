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
  private static final String SUBJECT_SCHEME_URI = "https://linked.data.gov.au/def/anzsrc-for/2020";

  @Mock
  private SubjectRepository subjectRepository;

  @InjectMocks
  private SubjectValidationService validationService;

  @Test
  void noFailuresWithValidCode() {
    final var id = "https://linked.data.gov.au/def/anzsrc-for/2020/222222";

    final var subject = new SubjectBlock()
      .subject(id)
      .subjectSchemeUri(SUBJECT_SCHEME_URI);

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

    final var subject = new SubjectBlock()
      .subject(id)
      .subjectSchemeUri(SUBJECT_SCHEME_URI);

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getMessage(), is(String.format("%s is not a valid field of research", id)));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getFieldId(), is("subjects[0].subject"));

    verifyNoInteractions(subjectRepository);
  }

  @Test
  void returnsFailureWithInvalidUrlPrefix() {
    final var id = "https://data.gov.au/def/anzsrc-for/2020/222222";

    final var subject = new SubjectBlock()
      .subject(id)
      .subjectSchemeUri(SUBJECT_SCHEME_URI);

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getMessage(), is(String.format("%s is not a valid field of research", id)));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getFieldId(), is("subjects[0].subject"));

    verifyNoInteractions(subjectRepository);
  }

  @Test
  void returnsFailureIfCodeNotFound() {
    final var id = "https://linked.data.gov.au/def/anzsrc-for/2020/222222";

    final var subject = new SubjectBlock()
      .subject(id)
      .subjectSchemeUri(SUBJECT_SCHEME_URI);

    when(subjectRepository.findById("222222")).thenReturn(Optional.empty());

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getMessage(), is(String.format("%s is not a standard FoR code", id)));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getFieldId(), is("subjects[0].subject"));
  }

  @Test
  void addsFailureWithInvalidMissingSubjectSchemeUri() {
    final var id = "https://linked.data.gov.au/def/anzsrc-for/2020/222222";

    final var subject = new SubjectBlock()
      .subject(id);

    when(subjectRepository.findById("222222")).thenReturn(Optional.of(new SubjectRecord()));

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getMessage(), is("must be https://linked.data.gov.au/def/anzsrc-for/2020."));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getFieldId(), is("subjects[0].subjectSchemeUri"));
  }

  @Test
  void addsFailureWithInvalidInvalidSubjectSchemeUri() {
    final var id = "https://linked.data.gov.au/def/anzsrc-for/2020/222222";

    final var subject = new SubjectBlock()
      .subject(id)
      .subjectSchemeUri("invalid");

    when(subjectRepository.findById("222222")).thenReturn(Optional.of(new SubjectRecord()));

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getMessage(), is("must be https://linked.data.gov.au/def/anzsrc-for/2020."));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getFieldId(), is("subjects[0].subjectSchemeUri"));
  }

  @Test
  void addsFailureWithInvalidInvalidSubjectAndMissingSubjectSchemeUri() {
    final var id = "https://linked.data.gov.au/def/anzsrc-for/2020/222222";

    final var subject = new SubjectBlock()
      .subject(id);

    when(subjectRepository.findById("222222")).thenReturn(Optional.empty());

    final List<ValidationFailure> validationFailures = validationService.validateSubjects(Collections.singletonList(subject));

    assertThat(validationFailures.size(), is(2));
    assertThat(validationFailures.get(0).getMessage(), is("must be https://linked.data.gov.au/def/anzsrc-for/2020."));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getFieldId(), is("subjects[0].subjectSchemeUri"));
    assertThat(validationFailures.get(1).getMessage(), is("https://linked.data.gov.au/def/anzsrc-for/2020/222222 is not a standard FoR code"));
    assertThat(validationFailures.get(1).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(1).getFieldId(), is("subjects[0].subject"));
  }
}

