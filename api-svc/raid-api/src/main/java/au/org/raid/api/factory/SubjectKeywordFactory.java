package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.SubjectKeyword;
import org.springframework.stereotype.Component;

@Component
public class SubjectKeywordFactory {
    public SubjectKeyword create(final String text, final Language language) {
        return new SubjectKeyword()
                .text(text)
                .language(language);
    }
}
