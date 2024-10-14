import { FormFieldProps } from "@/types";
import { getErrorMessageForField } from "@/utils";
import { Grid, TextField } from "@mui/material";
import { useController } from "react-hook-form";

export function TextInputField({
  formFieldProps,
  width = 12,
}: {
  formFieldProps: FormFieldProps;
  width?: number;
}) {
  const { field, formState } = useController(formFieldProps);
  const errors = formState.errors;
  const {
    label,
    placeholder,
    required,
    helperText,
    errorText,
    multiline = false,
  } = formFieldProps;

  const errorMessage = getErrorMessageForField(errors, field.name);
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
