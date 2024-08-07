import { RaidDto } from "@/generated/raid";
import { FormFieldProps } from "@/types";
import { getErrorMessageForField } from "@/utils";
import { Grid, TextField } from "@mui/material";
import { Control, Controller, FieldErrors } from "react-hook-form";

interface TextInputFieldProps {
  formFieldProps: FormFieldProps;
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  width?: number;
}

export function TextInputField({
  formFieldProps,
  control,
  errors,
  width = 12,
}: TextInputFieldProps) {
  const { name, label, placeholder, required, helperText, errorText } =
    formFieldProps;

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
            : required
            ? `${helperText} *`
            : helperText;

          return (
            <TextField
              {...field}
              error={!!errorMessage}
              fullWidth
              helperText={displayHelperText}
              label={label}
              placeholder={placeholder}
              required={!!required}
              size="small"
              variant="filled"
            />
          );
        }}
      />
    </Grid>
  );
}
