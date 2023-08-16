package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.AlternateUrl;
import raido.idl.raidv2.model.AlternateUrlBlock;

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