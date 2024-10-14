import { FormFieldProps } from "@/types";
import { Checkbox, FormControlLabel, FormGroup, Grid } from "@mui/material";
import { useController } from "react-hook-form";

export function CheckboxField({
  formFieldProps,
  width = 12,
}: {
  formFieldProps: FormFieldProps;
  width?: number;
}) {
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
