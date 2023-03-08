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

  private static final String URL_PREFIX = "https://linked.data.gov.au/def/anzsrc-for/2020/";
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
        if (subject.getId() == null) {
          final var failure = new ValidationFailure();
          failure.setFieldId(String.format("subjects[%d].id", i));
          failure.setMessage("Subject ID is required");
          failure.setErrorType("invalid");

          failures.add(failure);
        } else {
          final var subjectId = subject.getId().substring(subject.getId().lastIndexOf('/') + 1);

          if (!subject.getId().startsWith(URL_PREFIX) || subjectId.matches(".*\\D.*")) {
            final var failure = new ValidationFailure();
            failure.setFieldId(String.format("subjects[%d].id", i));
            failure.setMessage(String.format("%s is not a valid field of research", subject.getId()));
            failure.setErrorType("invalid");

            failures.add(failure);
          } else {
            final Optional<SubjectRecord> subjectRecord = subjectRepository.findById(subjectId);

            if (subjectRecord.isEmpty()) {
              final var failure = new ValidationFailure();
              failure.setFieldId(String.format("subjects[%d].id", i));
              failure.setMessage(String.format("%s is not a valid field of research", subject.getId()));
              failure.setErrorType("invalid");

              failures.add(failure);
            }
          }
        }
      });

    return failures;
  }
}
