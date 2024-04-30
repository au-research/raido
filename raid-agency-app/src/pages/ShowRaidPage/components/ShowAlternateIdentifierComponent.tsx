import { AlternateIdentifier } from "@/generated/raid";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

export default function ShowAlternateIdentifierComponent({
  alternateIdentifier,
}: {
  alternateIdentifier: AlternateIdentifier[] | undefined;
}) {
  return (
    <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
      <CardHeader title="Alternate Identifiers" />
      <CardContent>
        <Stack gap={3}>
          <Box>
            {(alternateIdentifier?.length === 0 ||
              alternateIdentifier === undefined) && (
              <Typography
                variant="body2"
                color="text.secondary"
                textAlign="center"
              >
                No alternate identifiers defined
              </Typography>
            )}
          </Box>
          {alternateIdentifier?.map((alternateIdentifier, index) => {
            return (
              <Stack sx={{ paddingLeft: 2 }} spacing={2} key={index}>
                <Box className="raid-card-well">
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={12} md={6}>
                      <Box>
                        <Typography variant="body2">Type</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {alternateIdentifier.type}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={12} md={6}>
                      <Box>
                        <Typography variant="body2">Title</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {alternateIdentifier.id}
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
