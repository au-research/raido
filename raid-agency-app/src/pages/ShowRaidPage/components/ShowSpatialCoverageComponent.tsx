import { SpatialCoverage } from "@/generated/raid";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

export default function ShowSpatialCoverageComponent({
  spatialCoverage,
}: {
  spatialCoverage: SpatialCoverage[] | undefined;
}) {
  return (
    <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
      <CardHeader title="Spatial Coverage" />
      <CardContent>
        <Stack gap={3}>
          <Box>
            {(spatialCoverage?.length === 0 ||
              spatialCoverage === undefined) && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No spatial coverage defined
              </Typography>
            )}
          </Box>
          {spatialCoverage?.map((item, index) => {
            return (
              <Stack spacing={2} key={index}>
                <Box className="raid-card-well">
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={12} md={7}>
                      <Box>
                        <Typography variant="body2">Place</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {JSON.stringify(item.place)}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={6} md={2}>
                      <Box>
                        <Typography variant="body2">Type</Typography>
                        <Typography
                          color="text.secondary"
                          variant="body1"
                          component="a"
                          href={item.id}
                          rel="noopener noreferrer"
                        >
                          {item.id}
                        </Typography>
                      </Box>
                    </Grid>
                  </Grid>
                </Box>
              </Stack>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
