import { DisplayItem } from "@/components/DisplayItem";
import { ModelDate } from "@/generated/raid";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import { Card, CardContent, CardHeader, Grid } from "@mui/material";

const DatesItem = ({ dates }: { dates: ModelDate }) => {
  return (
    <Grid container spacing={2}>
      <DisplayItem
        label="Start Date"
        value={dateDisplayFormatter(dates.startDate)}
        width={3}
      />
      <DisplayItem
        label="End Date"
        value={dateDisplayFormatter(dates.endDate)}
        width={3}
      />
    </Grid>
  );
};

export default function DateDisplayComponent({
  dates,
}: {
  dates: ModelDate | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Dates" />
      <CardContent>{dates && <DatesItem dates={dates} />}</CardContent>
    </Card>
  );
}
