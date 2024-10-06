import { TextInputField } from "@/fields/TextInputField";
import { RaidDto } from "@/generated/raid";
import { Grid } from "@mui/material";
import { Control, FieldErrors } from "react-hook-form";

interface DateDetailsFormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}
export default function DateDetailsFormComponent({
  control,
  errors,
}: DateDetailsFormComponentProps) {
  return (
    <Grid container spacing={2}>
      <TextInputField
        width={3}
        formFieldProps={{
          name: `date.startDate`,
          type: "text",
          label: "Start Date",
          placeholder: "Start Date",
          helperText: "",
          errorText: "",
        }}
      />
      <TextInputField
        width={3}
        formFieldProps={{
          name: `date.endDate`,
          type: "text",
          label: "End Date",
          placeholder: "End Date",
          helperText: "",
          errorText: "",
          required: false,
        }}
      />
    </Grid>
  );
}
