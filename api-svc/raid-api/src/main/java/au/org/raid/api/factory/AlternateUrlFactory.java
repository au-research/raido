package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.AlternateUrl;
import org.springframework.stereotype.Component;

@Component
public class AlternateUrlFactory {
    public AlternateUrl create(final String url) {
        return new AlternateUrl()
                .url(url);
    }
}