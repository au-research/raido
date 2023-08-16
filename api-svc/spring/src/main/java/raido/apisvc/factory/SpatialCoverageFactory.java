package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.SpatialCoverage;
import raido.idl.raidv2.model.SpatialCoverageBlock;

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