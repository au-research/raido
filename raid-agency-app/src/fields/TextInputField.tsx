import { RaidDto } from "@/generated/raid";
import { FormFieldProps } from "@/types";
import { getErrorMessageForField } from "@/utils";
import { Grid, TextField } from "@mui/material";
import { Control, FieldErrors, useController } from "react-hook-form";

interface TextInputFieldProps {
  formFieldProps: FormFieldProps;
  control?: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  width?: number;
}

export function TextInputField({
  formFieldProps,
  width = 12,
}: TextInputFieldProps) {
  const { field, formState } = useController(formFieldProps);
  const errorsNew = formState.errors;
  const {
    label,
    placeholder,
    required,
    helperText,
    errorText,
    multiline = false,
  } = formFieldProps;

  const errorMessage = getErrorMessageForField(errorsNew, field.name);
  const displayHelperText = errorMessage
    ? errorText
      ? errorText
      : errorMessage.message
    : required
    ? `${helperText} *`
    : helperText;

  return (
    <Grid item xs={width}>
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
        multiline={multiline}
        rows={multiline ? 5 : 1}
      />
    </Grid>
  );
}
