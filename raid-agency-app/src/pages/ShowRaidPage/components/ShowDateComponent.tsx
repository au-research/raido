import { DisplayItem } from "@/components/DisplayItem";
import { ModelDate } from "@/generated/raid";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
} from "@mui/material";

export default function ShowDateComponent({
  date,
}: {
  date: ModelDate | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Dates" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          <Box>
            <Grid container spacing={2}>
              <DisplayItem
                label="Start Date"
                value={dateDisplayFormatter(date?.startDate)}
                width={6}
              />
              <DisplayItem
                label="End Date"
                value={dateDisplayFormatter(date?.endDate) || "---"}
                width={6}
              />
            </Grid>
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
}
