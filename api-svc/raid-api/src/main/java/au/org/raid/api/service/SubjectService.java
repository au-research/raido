package au.org.raid.api.service;

import au.org.raid.api.factory.record.RaidSubjectKeywordRecordFactory;
import au.org.raid.api.factory.record.RaidSubjectRecordFactory;
import au.org.raid.api.repository.RaidSubjectKeywordRepository;
import au.org.raid.api.repository.RaidSubjectRepository;
import au.org.raid.api.repository.SubjectTypeRepository;
import au.org.raid.db.jooq.tables.records.RaidSubjectKeywordRecord;
import au.org.raid.idl.raidv2.model.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void create(final List<Subject> subjects, final String raidName) {
        for (final var subject : subjects) {

            final var subjectId = subject.getId().substring(subject.getId().lastIndexOf('/'));

            final var subjectTypeRecord = subjectTypeRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject type not found %s".formatted(subject.getId())));

            final var raidSubjectRecord = raidSubjectRecordFactory.create(raidName, subjectTypeRecord.getId());

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
}
