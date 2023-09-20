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

export default function ShowDescriptionComponent({
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
              Descriptions
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Box>
              {raid?.description?.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No descriptions defined
                </Typography>
              )}
            </Box>
            {raid?.description?.map((description, index) => {
              const descriptionType = extractKeyFromIdUri(
                description.type.id || ""
              );
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
                          <Typography variant="body2">{`Description (${descriptionType})`}</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {description.text}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={4}>
                        <Box>
                          <Typography variant="body2">Language</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {description.language?.id}
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
