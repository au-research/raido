import { Access } from "@/generated/raid";
import mapping from "@/mapping.json";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import dayjs from "dayjs";

import { MappingElement } from "@/types";
export default function ShowAccessComponent({ access }: { access: Access }) {
  return (
    <Card>
      <CardHeader title="Access" />
      <CardContent>
        <Stack gap={3}>
          <Box className="raid-card-well">
            <Grid container spacing={2}>
              <Grid item xs={12} sm={12} md={4}>
                <Box>
                  <Typography variant="body2">Access Statement</Typography>
                  <Typography color="text.secondary" variant="body1">
                    {access?.statement?.text}
                  </Typography>
                </Box>
              </Grid>
              <Grid item xs={12} sm={12} md={4}>
                <Box>
                  <Typography variant="body2">Language</Typography>
                  <Typography color="text.secondary" variant="body1">
                    {access?.statement?.language?.id}
                  </Typography>
                </Box>
              </Grid>
              <Grid item xs={12} sm={12} md={2}>
                <Box>
                  <Typography variant="body2">Access Type</Typography>
                  <Typography color="text.secondary" variant="body1">
                    {
                      mapping.find(
                        (el: MappingElement) => el.id === access.type.id
                      )?.value
                    }
                  </Typography>
                </Box>
              </Grid>

              {access?.type?.id.includes("c_f1cf") && (
                <Grid item xs={12} sm={12} md={2}>
                  <Box>
                    <Typography variant="body2">Embargo Expiry</Typography>
                    <Typography color="text.secondary" variant="body1">
                      {dateDisplayFormatter(
                        dayjs(access?.embargoExpiry).format("YYYY-MM-DD")
                      )}
                    </Typography>
                  </Box>
                </Grid>
              )}
            </Grid>
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
}
