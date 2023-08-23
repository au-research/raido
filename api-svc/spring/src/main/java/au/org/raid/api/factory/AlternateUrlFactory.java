package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.AlternateUrl;
import au.org.raid.idl.raidv2.model.AlternateUrlBlock;
import org.springframework.stereotype.Component;

@Component
public class AlternateUrlFactory {
    public AlternateUrl create(final AlternateUrlBlock alternateUrlBlock) {
        if (alternateUrlBlock == null) {
            return null;
        }

        return new AlternateUrl()
            .url(alternateUrlBlock.getUrl());
    }
}