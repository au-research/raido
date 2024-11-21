import DisplayCard from "@/components/DisplayCard";
import DisplayItem from "@/components/DisplayItem";
import type { SpatialCoverage } from "@/generated/raid";
import { Divider, Grid, Stack, Typography } from "@mui/material";
import { memo } from "react";
import SpatialCoveragePlaceItem from "./place/SpatialCoveragePlaceItem";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No spatial coverages defined
  </Typography>
));

const SpatialCoverageItem = memo(
  ({ spatialCoverage, i }: { spatialCoverage: SpatialCoverage; i: number }) => {
    return (
      <>
        <Typography variant="body1">Spatial Coverage #{i + 1}</Typography>
        <Grid container spacing={2}>
          <DisplayItem
            label="ID"
            link={spatialCoverage.id}
            value={spatialCoverage.id}
            width={12}
          />
        </Grid>
        <Stack gap={2} sx={{ pl: 3 }}>
          <Stack direction="row" alignItems="baseline">
            <Typography variant="body1" sx={{ mr: 0.5 }}>
              Places
            </Typography>
            <Typography variant="caption" color="text.disabled">
              Spatial Coverage #{i + 1}
            </Typography>
          </Stack>
          <Stack gap={2} divider={<Divider />}>
            {spatialCoverage?.place?.map((spatialCoveragePlace) => (
              <SpatialCoveragePlaceItem
                spatialCoveragePlace={spatialCoveragePlace}
                key={crypto.randomUUID()}
              />
            ))}
          </Stack>
        </Stack>
      </>
    );
  }
);

const SpatialCoverageDisplay = memo(({ data }: { data: SpatialCoverage[] }) => (
  <DisplayCard
    data={data}
    labelPlural="Spatial Coverages"
    children={
      <>
        {data.length === 0 && <NoItemsMessage />}
        <Stack gap={2} divider={<Divider />}>
          {data.map((spatialCoverage, i) => (
            <SpatialCoverageItem
              spatialCoverage={spatialCoverage}
              key={crypto.randomUUID()}
              i={i}
            />
          ))}
        </Stack>
      </>
    }
  />
));

NoItemsMessage.displayName = "NoItemsMessage";
SpatialCoverageItem.displayName = "SpatialCoverageItem";
SpatialCoverageDisplay.displayName = "SpatialCoverageDisplay";

export default SpatialCoverageDisplay;
