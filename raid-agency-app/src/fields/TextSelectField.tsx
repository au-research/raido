import { RaidDto } from "@/generated/raid";
import mapping from "@/mapping.json";
import { FormFieldProps } from "@/types";
import { getErrorMessageForField } from "@/utils";
import { Grid, MenuItem, TextField } from "@mui/material";
import { Control, Controller, FieldErrors } from "react-hook-form";

interface TextSelectFieldProps {
  formFieldProps: FormFieldProps;
  control: Control<RaidDto>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  options: any[];
  errors: FieldErrors<RaidDto>;
  width?: number;
}

export function TextSelectField({
  control,
  errors,
  formFieldProps,
  options,
  width = 12,
}: TextSelectFieldProps) {
  const { errorText, helperText, label, name, placeholder } = formFieldProps;

  return (
    <Grid item xs={width}>
      <Controller
        name={name as keyof RaidDto}
        control={control}
        render={({ field }) => {
          const errorMessage = getErrorMessageForField(errors, field.name);
          const displayHelperText = errorMessage
            ? errorText
              ? errorText
              : errorMessage.message
            : helperText;

          return (
            <TextField
              {...field}
              error={!!errorMessage}
              fullWidth
              helperText={displayHelperText}
              label={label}
              placeholder={placeholder}
              required
              select
              size="small"
              variant="filled"
            >
              {options.map((opt) => (
                <MenuItem key={opt.uri} value={opt.uri}>
                  {mapping.find((el) => el.id === opt.uri)?.value}
                </MenuItem>
              ))}
            </TextField>
          );
        }}
      />
    </Grid>
  );
}
