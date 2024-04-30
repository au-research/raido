import { AlternateUrl } from "@/generated/raid";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

export default function ShowAlternateUrlComponent({
  alternateUrl,
}: {
  alternateUrl: AlternateUrl[] | undefined;
}) {
  return (
    <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
      <CardHeader title="Alternate URLs" />
      <CardContent>
        <Stack gap={3}>
          <Box>
            {(alternateUrl?.length === 0 || alternateUrl === undefined) && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No alternate urls defined
              </Typography>
            )}
          </Box>
          {alternateUrl?.map((alternateUrl, index) => {
            return (
              <Stack sx={{ paddingLeft: 2 }} spacing={2} key={index}>
                <Box className="raid-card-well">
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={12} md={12}>
                      <Box>
                        <Typography variant="body2">Title</Typography>
                        <Typography
                          color="text.secondary"
                          variant="body1"
                          component="a"
                          href={alternateUrl.url}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          {alternateUrl.url}
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
