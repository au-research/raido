package au.org.raid.api.service;

import au.org.raid.api.factory.SubjectFactory;
import au.org.raid.api.factory.record.RaidSubjectKeywordRecordFactory;
import au.org.raid.api.factory.record.RaidSubjectRecordFactory;
import au.org.raid.api.repository.RaidSubjectKeywordRepository;
import au.org.raid.api.repository.RaidSubjectRepository;
import au.org.raid.api.repository.SubjectTypeRepository;
import au.org.raid.api.repository.SubjectTypeSchemaRepository;
import au.org.raid.db.jooq.tables.records.RaidSubjectKeywordRecord;
import au.org.raid.idl.raidv2.model.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SubjectService {
    private final LanguageService languageService;
    private final SubjectTypeRepository subjectTypeRepository;
    private final RaidSubjectRepository raidSubjectRepository;
    private final RaidSubjectRecordFactory raidSubjectRecordFactory;
    private final RaidSubjectKeywordRecordFactory raidSubjectKeywordRecordFactory;
    private final RaidSubjectKeywordRepository raidSubjectKeywordRepository;
    private final SubjectTypeSchemaRepository subjectTypeSchemaRepository;
    private final SubjectKeywordService subjectKeywordService;
    private final SubjectFactory subjectFactory;

    public void create(final List<Subject> subjects, final String handle) {
        if (subjects == null) {
            return;
        }

        for (final var subject : subjects) {

            final var subjectId = subject.getId().substring(subject.getId().lastIndexOf('/') + 1);

            final var subjectTypeRecord = subjectTypeRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject type not found %s".formatted(subject.getId())));

            final var raidSubjectRecord = raidSubjectRecordFactory.create(handle, subjectTypeRecord.getId());

            final var raidSubject = raidSubjectRepository.create(raidSubjectRecord);

            for (final var keyword : subject.getKeyword()) {
                Integer languageId = null;
                if (keyword.getLanguage() != null) {
                    languageId = languageService.findLanguageId(keyword.getLanguage());
                }

                final var raidSubjectKeywordRecord =
                        raidSubjectKeywordRecordFactory.create(raidSubject.getId(), keyword.getText(), languageId);

                raidSubjectKeywordRepository.create(raidSubjectKeywordRecord);
            }
        }
    }

    public List<Subject> findAllByHandle(final String handle) {
        final var subjects = new ArrayList<Subject>();
        final var records = raidSubjectRepository.findAllByHandle(handle);

        for (final var record : records) {
            final var typeRecord = subjectTypeRepository.findById(record.getSubjectTypeId())
                    .orElseThrow(() -> new RuntimeException("Subject type not found with id %s".formatted(record.getSubjectTypeId())));

            final var schemaId = typeRecord.getSchemaId();

            final var typeSchemaRecord = subjectTypeSchemaRepository.findById(schemaId)
                    .orElseThrow(() -> new RuntimeException("Subject type schema not found with id %d".formatted(schemaId)));

            final var keywords = subjectKeywordService.findAllByRaidSubjectId(record.getId());

            subjects.add(subjectFactory.create(typeRecord.getId(), typeSchemaRecord.getUri(), keywords));
        }
        return subjects;
    }
}
