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
import { extractKeyFromIdUri } from "utils";

export default function ShowTitleComponent({
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
              Titles
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Box>
              {raid?.title?.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No titles defined
                </Typography>
              )}
            </Box>
            {raid?.title?.map((title, index) => {
              const titleType = extractKeyFromIdUri(title.type.id || "");
              return (
                <Stack sx={{ paddingLeft: 2 }} spacing={2} key={index}>
                  <Box
                    sx={{
                      bgcolor: "rgba(0, 0, 0, 0.02)",
                      p: 2,
                      borderRadius: 2,
                    }}
                    className="animated-tile animated-tile-reverse"
                  >
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={12} md={12}>
                        <Box>
                          <Typography variant="body2">{`Title (${titleType})`}</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {title.text}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={4}>
                        <Box>
                          <Typography variant="body2">Language</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {title.language?.id}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={4}>
                        <Box>
                          <Typography variant="body2">Start Date</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {title.startDate}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={4}>
                        <Box>
                          <Typography variant="body2">End Date</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {title.endDate}
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
    </Box>
  );
}
