package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.AccessStatement;
import au.org.raid.idl.raidv2.model.Language;
import org.springframework.stereotype.Component;

@Component
public class AccessStatementFactory {
    public AccessStatement create(final String text, final Language language) {
        return new AccessStatement()
                .text(text)
                .language(language);
    }
}
