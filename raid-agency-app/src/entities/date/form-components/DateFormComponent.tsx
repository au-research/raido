import DateDetailsFormComponent from "@/entities/date/form-components/DateDetailsFormComponent";
import { RaidDto } from "@/generated/raid";
import { Card, CardContent, CardHeader } from "@mui/material";
import { Control, FieldErrors, UseFormTrigger } from "react-hook-form";

interface DateFormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}

export default function DateFormComponent({
  control,
  errors,
}: DateFormComponentProps) {
  return (
    <Card
      sx={{
        borderLeft: errors.title ? "3px solid" : "none",
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader title="Dates" />
      <CardContent>
        <DateDetailsFormComponent control={control} errors={errors} />
      </CardContent>
    </Card>
  );
}
