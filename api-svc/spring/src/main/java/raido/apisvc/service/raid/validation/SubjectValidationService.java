package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.repository.SubjectRepository;
import raido.db.jooq.api_svc.tables.records.SubjectRecord;
import raido.idl.raidv2.model.SubjectBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static raido.apisvc.util.ObjectUtil.indexed;

@Component
public class SubjectValidationService {

  private static final String SUBJECT_SCHEME_URI = "https://linked.data.gov.au/def/anzsrc-for/2020";
  private final SubjectRepository subjectRepository;

  public SubjectValidationService(final SubjectRepository subjectRepository) {
    this.subjectRepository = subjectRepository;
  }

  public List<ValidationFailure> validateSubjects(List<SubjectBlock> subjects) {

    final var failures = new ArrayList<ValidationFailure>();

    if (subjects == null) {
      return failures;
    }

    subjects.stream().
      collect(indexed()).
      forEach((i, subject)->{
        if (subject.getSubjectSchemeUri() == null || !subject.getSubjectSchemeUri().equals(SUBJECT_SCHEME_URI)) {
          final var failure = new ValidationFailure();
          failure.setFieldId(String.format("subjects[%d].subjectSchemeUri", i));
          failure.setMessage(String.format("must be %s.", SUBJECT_SCHEME_URI));
          failure.setErrorType("invalid");

          failures.add(failure);
        }

        if (subject.getSubject() == null) {
          final var failure = new ValidationFailure();
          failure.setFieldId(String.format("subjects[%d].subject", i));
          failure.setMessage("Subject field is required");
          failure.setErrorType("invalid");

          failures.add(failure);
        } else {
          final var subjectId = subject.getSubject().substring(subject.getSubject().lastIndexOf('/') + 1);

          if (!subject.getSubject().startsWith(SUBJECT_SCHEME_URI) || subjectId.matches(".*\\D.*")) {
            final var failure = new ValidationFailure();
            failure.setFieldId(String.format("subjects[%d].subject", i));
            failure.setMessage(String.format("%s is not a valid field of research", subject.getSubject()));
            failure.setErrorType("invalid");

            failures.add(failure);
          } else {
            final Optional<SubjectRecord> subjectRecord = subjectRepository.findById(subjectId);

            if (subjectRecord.isEmpty()) {
              final var failure = new ValidationFailure();
              failure.setFieldId(String.format("subjects[%d].subject", i));
              failure.setMessage(String.format("%s is not a standard FoR code", subject.getSubject()));
              failure.setErrorType("invalid");

              failures.add(failure);
            }
          }
        }
      });

    return failures;
  }
}
