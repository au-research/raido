package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.SpatialCoverage;
import au.org.raid.idl.raidv2.model.SpatialCoverageBlock;
import au.org.raid.idl.raidv2.model.SpatialCoveragePlace;
import org.springframework.stereotype.Component;

import java.util.List;

import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class SpatialCoverageFactory {

    public SpatialCoverage create(final SpatialCoverageBlock spatialCoverageBlock) {
        if (spatialCoverageBlock == null) {
            return null;
        }

        List<SpatialCoveragePlace> places = null;

        if (!isBlank(spatialCoverageBlock.getSpatialCoveragePlace())) {
            places = List.of(new SpatialCoveragePlace()
                    .text(spatialCoverageBlock.getSpatialCoveragePlace()));
        }

        return new SpatialCoverage()
                .id(spatialCoverageBlock.getSpatialCoverage())
                .schemaUri(spatialCoverageBlock.getSpatialCoverageSchemeUri())
                .place(places);
    }

    public SpatialCoverage create(final String id, final String schemaUri, final List<SpatialCoveragePlace> places) {
        return new SpatialCoverage()
                .id(id)
                .schemaUri(schemaUri)
                .place(places);

    }
}