import { RaidDto } from "@/generated/raid";
import { Failure } from "@/types";
import { Card, CardContent, CardHeader } from "@mui/material";
import { Control, FieldErrors } from "react-hook-form";
import DateDetailsForm from "./DateDetailsForm";

export default function DatesForm({
  control,
  errors,
  apiValidationErrors,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  apiValidationErrors?: Failure[];
}) {
  return (
    <Card
      sx={{
        borderLeft: "solid",
        borderLeftWidth: errors.date || apiValidationErrors ? 3 : 0,
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader title="Dates" />
      <CardContent>
        {/* <Box className="raid-card-well"> */}
          <DateDetailsForm control={control} errors={errors} />
        {/* </Box> */}
      </CardContent>
    </Card>
  );
}
