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

export default function ShowDateComponent({
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
              Dates
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Box
              sx={{
                bgcolor: "rgba(0, 0, 0, 0.02)",
                p: 2,
                borderRadius: 2,
              }}
              className="animated-tile animated-tile-reverse"
            >
              <Grid container spacing={2}>
                <Grid item xs={12} sm={12} md={6}>
                  <Box>
                    <Typography variant="body2">Start Date</Typography>
                    <Typography color="text.secondary" variant="body1">
                      {dateDisplayFormatter(raid?.date?.startDate)}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12} sm={12} md={6}>
                  <Box>
                    <Typography variant="body2">End Date</Typography>
                    <Typography color="text.secondary" variant="body1">
                      {dateDisplayFormatter(raid?.date?.endDate)}
                    </Typography>
                  </Box>
                </Grid>
              </Grid>
            </Box>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
