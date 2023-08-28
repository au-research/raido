package au.org.raid.api.validator;

import au.org.raid.api.repository.SubjectTypeRepository;
import au.org.raid.db.jooq.api_svc.tables.records.SubjectTypeRecord;
import au.org.raid.idl.raidv2.model.Subject;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.ObjectUtil.indexed;

@Component
public class SubjectValidator {

    private static final String SUBJECT_SCHEME_URI = "https://linked.data.gov.au/def/anzsrc-for/2020/";
    private final SubjectTypeRepository subjectTypeRepository;

    public SubjectValidator(final SubjectTypeRepository subjectTypeRepository) {
        this.subjectTypeRepository = subjectTypeRepository;
    }

    public List<ValidationFailure> validate(List<Subject> subjects) {

        final var failures = new ArrayList<ValidationFailure>();

        if (subjects == null) {
            return failures;
        }

        subjects.stream().
                collect(indexed()).
                forEach((i, subject) -> {
                    if (subject.getSchemaUri() == null || !subject.getSchemaUri().equals(SUBJECT_SCHEME_URI)) {
                        final var failure = new ValidationFailure();
                        failure.setFieldId(String.format("subjects[%d].schemaUri", i));
                        failure.setMessage(String.format("must be %s.", SUBJECT_SCHEME_URI));
                        failure.setErrorType(INVALID_VALUE_TYPE);

                        failures.add(failure);
                    }

                    if (subject.getId() == null) {
                        final var failure = new ValidationFailure();
                        failure.setFieldId(String.format("subjects[%d].id", i));
                        failure.setMessage(NOT_SET_MESSAGE);
                        failure.setErrorType(NOT_SET_TYPE);

                        failures.add(failure);
                    } else {
                        final var subjectId = subject.getId().substring(subject.getId().lastIndexOf('/') + 1);

                        if (!subject.getId().startsWith(SUBJECT_SCHEME_URI) || subjectId.matches(".*\\D.*")) {
                            final var failure = new ValidationFailure();
                            failure.setFieldId(String.format("subjects[%d].id", i));
                            failure.setMessage(String.format("%s is not a valid field of research", subject.getId()));
                            failure.setErrorType(INVALID_VALUE_TYPE);

                            failures.add(failure);
                        } else {
                            final Optional<SubjectTypeRecord> subjectTypeRecord = subjectTypeRepository.findById(subjectId);

                            if (subjectTypeRecord.isEmpty()) {
                                final var failure = new ValidationFailure();
                                failure.setFieldId(String.format("subjects[%d].id", i));
                                failure.setMessage(String.format("%s is not a standard FoR code", subject.getId()));
                                failure.setErrorType(INVALID_VALUE_TYPE);

                                failures.add(failure);
                            }
                        }
                    }
                });

        return failures;
    }
}