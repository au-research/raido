package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.SpatialCoverage;
import au.org.raid.idl.raidv2.model.SpatialCoverageBlock;
import org.springframework.stereotype.Component;

@Component
public class SpatialCoverageFactory {

    public SpatialCoverage create(final SpatialCoverageBlock spatialCoverageBlock) {
        if (spatialCoverageBlock == null) {
            return null;
        }

        return new SpatialCoverage()
                .id(spatialCoverageBlock.getSpatialCoverage())
                .schemeUri(spatialCoverageBlock.getSpatialCoverageSchemeUri())
                .place(spatialCoverageBlock.getSpatialCoveragePlace());
    }
}