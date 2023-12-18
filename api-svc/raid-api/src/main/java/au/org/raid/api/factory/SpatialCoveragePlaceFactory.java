package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.SpatialCoveragePlace;
import org.springframework.stereotype.Component;

@Component
public class SpatialCoveragePlaceFactory {
    public SpatialCoveragePlace create(final String text, final Language language) {
        return new SpatialCoveragePlace()
                .text(text)
                .language(language);
    }
}
