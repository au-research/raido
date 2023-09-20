import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";
import { Launch as LaunchIcon } from "@mui/icons-material";
import ScatterPlotIcon from "@mui/icons-material/ScatterPlot";
import { Box, Link } from "@mui/material";
import Grid from "@mui/material/Grid";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import CategoryHeadingBox from "../../../components/category-heading-box";
import { ReadData } from "types";
import { RaidDto } from "Generated/Raidv2";

export default function Meta({ data }: { data: RaidDto | undefined }) {
  return (
    <Grid container alignItems="stretch">
      <Grid item xs={12} sm={3} md={3}>
        <CategoryHeadingBox
          title="Meta"
          subtitle="RAiD meta data"
          IconComponent={ScatterPlotIcon}
        />
      </Grid>
      <Grid item xs={12} sm={9} md={9}>
        <Grid container>
          <Grid item sm={12} md={4} xs={12} px={2} py={1}>
            <Box>
              <Typography variant="body2">
                CNRI URL
                <Tooltip title="More info about this field..." placement="top">
                  <InfoOutlinedIcon
                    sx={{
                      color: "text.secondary",
                      fontSize: "0.8rem",
                      mb: 1,
                    }}
                  />
                </Tooltip>
              </Typography>
              <Typography
                color="text.secondary"
                variant="body1"
                component={Link}
                href={`https://hdl.handle.net/${data?.identifier?.id}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                {data?.identifier?.id} <LaunchIcon sx={{ fontSize: 12 }} />
              </Typography>
            </Box>
          </Grid>
          <Grid item sm={12} md={4} xs={12} px={2} py={1}>
            <Box>
              <Typography variant="body2">
                Agency URL
                <Tooltip title="More info about this field..." placement="top">
                  <InfoOutlinedIcon
                    sx={{
                      color: "text.secondary",
                      fontSize: "0.8rem",
                      mb: 1,
                    }}
                  />
                </Tooltip>
              </Typography>
              <Typography
                color="text.secondary"
                variant="body1"
                component={Link}
                href={`http://localhost:8080/${data?.identifier?.id}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                {data?.identifier?.id} <LaunchIcon sx={{ fontSize: 12 }} />
              </Typography>
            </Box>
          </Grid>

          <Grid item sm={12} md={4} xs={12} px={2} py={1}>
            <Box>
              <Typography variant="body2">Service point</Typography>
              <Typography color="text.secondary" variant="body1">
                n/a
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
}
