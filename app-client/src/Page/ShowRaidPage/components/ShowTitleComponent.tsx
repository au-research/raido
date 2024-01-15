import {Box, Card, CardContent, CardHeader, Grid, Stack, Typography,} from "@mui/material";
import {type Title} from "Generated/Raidv2";
import {dateDisplayFormatter} from "date-utils";
import {extractKeyFromIdUri, raidColors} from "utils";
import language from "../../../References/language.json";

export default function ShowTitleComponent({ titles }: { titles: Title[] }) {
  return (
    <Box sx={{ paddingLeft: 2 }}>
      <Card
        variant="outlined"
        sx={{
          borderLeft: "solid",
          borderLeftColor: raidColors.get("blue"),
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
              {titles?.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No titles defined
                </Typography>
              )}
            </Box>
            {titles?.map((title, index) => {
              const titleType = extractKeyFromIdUri(title.type.id || "");
              const lang = language.find(
                (language) => language.id === title?.language?.id,
              );

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
                      <Grid item xs={12} sm={12} md={12}>
                        <Box>
                          <Typography variant="body2">Title</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {title.text}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={6} md={6}>
                        <Box>
                          <Typography variant="body2">Type</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {extractKeyFromIdUri(title.type.id)}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={6} md={3}>
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
