package au.org.raid.api.service;

import au.org.raid.api.factory.SubjectKeywordFactory;
import au.org.raid.api.repository.RaidSubjectKeywordRepository;
import au.org.raid.db.jooq.tables.records.RaidSubjectKeywordRecord;
import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.SubjectKeyword;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectKeywordServiceTest {
    @Mock
    private RaidSubjectKeywordRepository raidSubjectKeywordRepository;
    @Mock
    private LanguageService languageService;
    @Mock
    private SubjectKeywordFactory subjectKeywordFactory;
    @InjectMocks
    private SubjectKeywordService subjectKeywordService;

    @Test
    void findAllByRaidSubjectId() {
        final var raidSubjectId = 123;
        final var languageId = 234;
        final var keyword = "_keyword";

        final var raidSubjectKeywordRecord = new RaidSubjectKeywordRecord()
                .setLanguageId(languageId)
                .setKeyword(keyword);

        final var language = new Language();

        final var subjectKeyword = new SubjectKeyword();

        when(raidSubjectKeywordRepository.findAllByRaidSubjectId(raidSubjectId))
                .thenReturn(List.of(raidSubjectKeywordRecord));

        when(languageService.findById(languageId)).thenReturn(language);

        when(subjectKeywordFactory.create(keyword, language)).thenReturn(subjectKeyword);

        assertThat(subjectKeywordService.findAllByRaidSubjectId(raidSubjectId), is(List.of(subjectKeyword)));
    }
}