import { RelatedRaid } from "@/generated/raid";
import { extractKeyFromIdUri } from "@/utils";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

export default function ShowRelatedRaidComponent({
  relatedRaid,
}: {
  relatedRaid: RelatedRaid[] | undefined;
}) {
  return (
    <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
      <CardHeader title="Related RAiDs" />
      <CardContent>
        <Stack gap={3}>
          <Box>
            {(relatedRaid?.length === 0 || relatedRaid === undefined) && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No related RAiDs defined
              </Typography>
            )}
          </Box>
          {relatedRaid?.map((relatedRaid, index) => {
            const raidHandle = new URL(
              relatedRaid?.id || ""
            ).pathname.substring(1);

            return (
              <Stack sx={{ paddingLeft: 2 }} spacing={2} key={index}>
                <Box className="raid-card-well">
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={12} md={6}>
                      <Box>
                        <Typography variant="body2">ID</Typography>
                        <Typography color="text.secondary" variant="body1">
                          <a href={`/show-raid/${raidHandle}`}>
                            {`${raidHandle}`}
                          </a>
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={12} md={3}>
                      <Box>
                        <Typography variant="body2">Type</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {extractKeyFromIdUri(relatedRaid.type?.id)}
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
