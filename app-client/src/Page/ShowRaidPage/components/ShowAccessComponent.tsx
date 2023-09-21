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
import { languages } from "../../../Page/languages";

export default function ShowAccessComponent({
  raid,
  color,
}: {
  raid: RaidDto;
  color: string;
}) {
  const language = languages.find(
    (language) => language.id === raid?.access?.accessStatement?.language?.id
  );

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
              Access
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
                <Grid item xs={12} sm={12} md={10}>
                  <Box>
                    <Typography variant="body2">
                      Access Statement (
                      {extractKeyFromIdUri(raid?.access?.type?.id)})
                    </Typography>
                    <Typography color="text.secondary" variant="body1">
                      {raid?.access?.accessStatement?.text}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12} sm={12} md={2}>
                  <Box>
                    <Typography variant="body2">Language</Typography>
                    <Typography color="text.secondary" variant="body1">
                      {language?.name}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12} sm={12} md={2}>
                  <Box>
                    <Typography variant="body2">Embargo Expiry</Typography>
                    <Typography color="text.secondary" variant="body1">
                      {raid?.access?.embargoExpiry?.toString()}
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
