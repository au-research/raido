package au.org.raid.api.service;

import au.org.raid.api.factory.SubjectKeywordFactory;
import au.org.raid.api.repository.RaidSubjectKeywordRepository;
import au.org.raid.idl.raidv2.model.SubjectKeyword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SubjectKeywordService {
    private final RaidSubjectKeywordRepository raidSubjectKeywordRepository;
    private final LanguageService languageService;
    private final SubjectKeywordFactory subjectKeywordFactory;

    public List<SubjectKeyword> findAllByRaidSubjectId(final Integer raidSubjectId) {
        final var keywords = new ArrayList<SubjectKeyword>();
        final var records = raidSubjectKeywordRepository.findAllByRaidSubjectId(raidSubjectId);

        for (final var record : records) {
            final var language = languageService.findById(record.getLanguageId());

            keywords.add(subjectKeywordFactory.create(record.getKeyword(), language));
        }
        return keywords;
    }
}
