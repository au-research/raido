import { ScatterPlot as ScatterPlotIcon } from "@mui/icons-material";

import Grid from "@mui/material/Grid";
import {
  LegacyMetadataSchemaV1,
  RaidDto,
  RaidoMetadataSchemaV1,
  RaidoMetadataSchemaV2,
  ReadRaidResponseV2,
} from "Generated/Raidv2/models";
import CategoryHeadingBox from "../../../components/category-heading-box";
import Descriptions from "./components/descriptions";
import Titles from "./components/titles";
import { Divider, Stack } from "@mui/material";

interface ReadData {
  readonly raid: ReadRaidResponseV2;
  readonly metadata:
    | RaidoMetadataSchemaV1
    | RaidoMetadataSchemaV2
    | LegacyMetadataSchemaV1;
}

export default function Main({ data }: { data: RaidDto | undefined }) {
  return (
    <Grid container alignItems="stretch">
      <Grid item xs={12} sm={3} md={3}>
        <CategoryHeadingBox
          title="Main"
          subtitle="RAiD main data"
          IconComponent={ScatterPlotIcon}
        />
      </Grid>
      <Grid item xs={12} sm={9} md={9}>
        <Grid container>
          <Grid item sm={12} md={12} xs={12} px={2} py={1}>
            <Stack gap={2} divider={<Divider />}>
              <Titles data={data} />
              <Descriptions data={data} />
            </Stack>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
}
