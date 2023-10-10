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

export default function ShowRelatedRaisComponent({
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
              Related Objects
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Box>
              {raid?.relatedObject?.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No related objects defined
                </Typography>
              )}
            </Box>
            {raid?.relatedObject?.map((relatedObject, index) => {
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
                      <Grid item xs={12} sm={12} md={6}>
                        <Box>
                          <Typography variant="body2">ID</Typography>
                          <Typography color="text.secondary" variant="body1">
                            <a href={relatedObject.id}>{relatedObject.id}</a>
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={3}>
                        <Box>
                          <Typography variant="body2">Type</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {extractKeyFromIdUri(relatedObject.type?.id)}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={3}>
                        <Box>
                          <Typography variant="body2">Category</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {extractKeyFromIdUri(relatedObject.category?.id)}
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
