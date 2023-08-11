import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";
import ScatterPlotIcon from "@mui/icons-material/ScatterPlot";
import { Box } from "@mui/material";
import Grid from "@mui/material/Grid";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import CategoryHeadingBox from "./category-heading-box";

export default function Meta({ theme }: { theme: any }) {
  return (
    <Grid container alignItems="stretch">
      <Grid item xs={12} sm={3} md={3}>
        <CategoryHeadingBox
          theme={theme}
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
                      color: theme.palette.text.secondary,
                      fontSize: "0.8rem",
                      mb: 1,
                    }}
                  />
                </Tooltip>
              </Typography>
              <Typography color="text.secondary" variant="body1">
                10378.1/1724446
              </Typography>
            </Box>
          </Grid>
          <Grid item sm={12} md={4} xs={12} px={2} py={1}>
            <Box>
              <Typography variant="body2">Agency URL</Typography>
              <Typography color="text.secondary" variant="body1">
                10378.1/1724446
              </Typography>
            </Box>
          </Grid>
          <Grid item sm={12} md={4} xs={12} px={2} py={1}>
            <Box>
              <Typography variant="body2">Service point</Typography>
              <Typography color="text.secondary" variant="body1">
                raido
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
}
