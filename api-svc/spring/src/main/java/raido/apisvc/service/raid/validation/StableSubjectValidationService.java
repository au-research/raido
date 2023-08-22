package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.repository.SubjectTypeRepository;
import raido.db.jooq.api_svc.tables.records.SubjectTypeRecord;
import raido.idl.raidv2.model.Subject;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static raido.apisvc.util.ObjectUtil.indexed;

@Component
public class StableSubjectValidationService {

  private static final String SUBJECT_SCHEME_URI = "https://linked.data.gov.au/def/anzsrc-for/2020/";
  private final SubjectTypeRepository subjectTypeRepository;

  public StableSubjectValidationService(final SubjectTypeRepository subjectTypeRepository) {
    this.subjectTypeRepository = subjectTypeRepository;
  }

  public List<ValidationFailure> validateSubjects(List<Subject> subjects) {

    final var failures = new ArrayList<ValidationFailure>();

    if (subjects == null) {
      return failures;
    }

    subjects.stream().
      collect(indexed()).
      forEach((i, subject)->{
        if (subject.getSchemeUri() == null || !subject.getSchemeUri().equals(SUBJECT_SCHEME_URI)) {
          final var failure = new ValidationFailure();
          failure.setFieldId(String.format("subjects[%d].subjectSchemeUri", i));
          failure.setMessage(String.format("must be %s.", SUBJECT_SCHEME_URI));
          failure.setErrorType("invalid");

          failures.add(failure);
        }

        if (subject.getId() == null) {
          final var failure = new ValidationFailure();
          failure.setFieldId(String.format("subjects[%d].id", i));
          failure.setMessage("Subject field is required");
          failure.setErrorType("invalid");

          failures.add(failure);
        } else {
          final var subjectId = subject.getId().substring(subject.getId().lastIndexOf('/') + 1);

          if (!subject.getId().startsWith(SUBJECT_SCHEME_URI) || subjectId.matches(".*\\D.*")) {
            final var failure = new ValidationFailure();
            failure.setFieldId(String.format("subjects[%d].id", i));
            failure.setMessage(String.format("%s is not a valid field of research", subject.getId()));
            failure.setErrorType("invalid");

            failures.add(failure);
          } else {
            final Optional<SubjectTypeRecord> subjectTypeRecord = subjectTypeRepository.findById(subjectId);

            if (subjectTypeRecord.isEmpty()) {
              final var failure = new ValidationFailure();
              failure.setFieldId(String.format("subjects[%d].id", i));
              failure.setMessage(String.format("%s is not a standard FoR code", subject.getId()));
              failure.setErrorType("invalid");

              failures.add(failure);
            }
          }
        }
      });

    return failures;
  }
}