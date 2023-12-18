package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidDescriptionRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidDescriptionRecordFactory {
    public RaidDescriptionRecord create(final String handle, final String text, final Integer descriptionTypeId, final Integer languageId) {
        return new RaidDescriptionRecord()
                .setHandle(handle)
                .setText(text)
                .setDescriptionTypeId(descriptionTypeId)
                .setLanguageId(languageId);
    }
}
