package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidTitleRecord;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

@Component
public class RaidTitleRecordFactory {

    public RaidTitleRecord create(final Title title, final String raidName, final Integer titleTypeId, final Integer languageId) {
        return new RaidTitleRecord()
                .setRaidName(raidName)
                .setText(title.getText())
                .setStartDate(title.getStartDate())
                .setEndDate(title.getEndDate())
                .setLanguageId(languageId)
                .setTitleTypeId(titleTypeId);
    }
}
