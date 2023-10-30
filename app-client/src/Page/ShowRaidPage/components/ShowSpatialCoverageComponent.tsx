import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";
import { dateDisplayFormatter } from "date-utils";
import { extractKeyFromIdUri } from "utils";
import language from "../../../References/language.json";

export default function ShowSpatialCoverageComponent({
  raid,
  color,
}: {
  raid: RaidDto;
  color: string;
}) {
  return (
    <Box sx={{ paddingLeft: 2 }}>
      <Card
        variant="outlined"
        sx={{
          borderLeft: "solid",
          borderLeftColor: color,
          borderLeftWidth: 3,
        }}
      >
        <CardHeader
          title={
            <Typography variant="h6" component="div">
              Spatial Coverage
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Box>
              {raid?.spatialCoverage?.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No titles defined
                </Typography>
              )}
            </Box>
            {raid?.spatialCoverage?.map((item, index) => {
              return (
                <Stack spacing={2} key={index}>
                  <Box
                    sx={{
                      bgcolor: "rgba(0, 0, 0, 0.02)",
                      p: 2,
                      borderRadius: 2,
                    }}
                    className="animated-tile animated-tile-reverse"
                  >
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={12} md={7}>
                        <Box>
                          <Typography variant="body2">Place</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {item.place}
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
                      {/* <Grid item xs={12} sm={6} md={3}>
                        <Box>
                          <Typography variant="body2">Language</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {lang?.name}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={6} md={6}>
                        <Box>
                          <Typography variant="body2">Start Date</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {dateDisplayFormatter(title.startDate)}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={6} md={3}>
                        <Box>
                          <Typography variant="body2">End Date</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {dateDisplayFormatter(title.endDate)}
                          </Typography>
                        </Box>
                      </Grid> */}
                    </Grid>
                  </Box>
                </Stack>
              );
            })}
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
