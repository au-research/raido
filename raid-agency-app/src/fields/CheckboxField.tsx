import { RaidDto } from "@/generated/raid";
import { FormFieldProps } from "@/types";
import { Checkbox, FormControlLabel, FormGroup, Grid } from "@mui/material";
import { Control, FieldErrors, useController } from "react-hook-form";

interface CheckboxFieldProps {
  formFieldProps: FormFieldProps;
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  width?: number;
}

export function CheckboxField({
  formFieldProps,
  width = 12,
}: CheckboxFieldProps) {
  const { label, required } = formFieldProps;
  const { field } = useController(formFieldProps);

  return (
    <Grid item xs={width}>
      <FormGroup>
        <FormControlLabel
          control={<Checkbox {...field} checked={!!field.value} />}
          label={label}
          required={!!required}
        />
      </FormGroup>
    </Grid>
  );
}
