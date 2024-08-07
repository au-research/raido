import { TextInputField } from "@/fields/TextInputField";
import { RaidDto } from "@/generated/raid";
import { Grid } from "@mui/material";
import { Control, FieldErrors } from "react-hook-form";

export default function DateDetailsForm({
  control,
  errors,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}) {
  return (
    <Grid container spacing={2}>
      <Grid item xs={12} sm={6}>
        <TextInputField
          control={control}
          errors={errors}
          formFieldProps={{
            name: "date.startDate",
            type: "text",
            label: "Start Date",
            placeholder: "Start Date",
            helperText: "YYYY or YYYY-MM or YYYY-MM-DD",
            errorText: "must be YYYY or YYYY-MM or YYYY-MM-DD",
            required: true,
          }}
        />
      </Grid>
      <Grid item xs={12} sm={6}>
        <TextInputField
          control={control}
          errors={errors}
          formFieldProps={{
            name: "date.endDate",
            type: "text",
            label: "End Date",
            placeholder: "End Date",
            helperText: "YYYY or YYYY-MM or YYYY-MM-DD",
            errorText: "must be YYYY or YYYY-MM or YYYY-MM-DD",
            required: false,
          }}
        />
      </Grid>
    </Grid>
  );
}
