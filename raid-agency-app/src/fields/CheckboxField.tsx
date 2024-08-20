import { RaidDto } from "@/generated/raid";
import { FormFieldProps } from "@/types";
import { Checkbox, FormControlLabel, FormGroup, Grid } from "@mui/material";
import { Control, Controller, FieldErrors } from "react-hook-form";

interface CheckboxFieldProps {
  formFieldProps: FormFieldProps;
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  width?: number;
}

export function CheckboxField({
  formFieldProps,
  control,
  width = 12,
}: CheckboxFieldProps) {
  const { name, label, required } = formFieldProps;

  return (
    <Grid item xs={width}>
      <Controller
        name={name as keyof RaidDto}
        control={control}
        render={({ field: props }) => {
          return (
            <>
              <FormGroup>
                <FormControlLabel
                  control={
                    <Checkbox
                      {...props}
                      checked={!!props.value}
                      onChange={(e) => {
                        const newValue = e.target.checked;
                        props.onChange(newValue);
                      }}
                    />
                  }
                  label={label}
                  required={!!required}
                />
              </FormGroup>
            </>
          );
        }}
      />
    </Grid>
  );
}
