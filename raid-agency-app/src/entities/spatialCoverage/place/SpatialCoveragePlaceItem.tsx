import { DisplayItem } from "@/components/display-item";
import { useMapping } from "@/mapping";
import { SpatialCoveragePlace } from "@/generated/raid";
import { Grid } from "@mui/material";
import { memo, useMemo } from "react";

const SpatialCoveragePlaceItem = memo(
  ({
    spatialCoveragePlace,
  }: {
    spatialCoveragePlace: SpatialCoveragePlace;
  }) => {
    const { languageMap } = useMapping();

    const languageMappedValue = useMemo(
      () => languageMap.get(String(spatialCoveragePlace.language?.id)) ?? "",
      [spatialCoveragePlace.language?.id]
    );
    return (
      <Grid container spacing={2}>
        <DisplayItem
          label="Place"
          value={spatialCoveragePlace.text}
          width={8}
        />
        <DisplayItem label="Language" value={languageMappedValue} width={4} />
      </Grid>
    );
  }
);

SpatialCoveragePlaceItem.displayName = "SpatialCoveragePlaceItem";

export default SpatialCoveragePlaceItem;
