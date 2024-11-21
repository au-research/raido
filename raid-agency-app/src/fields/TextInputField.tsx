import { getErrorMessageForField } from "@/utils";
import { Grid, TextField } from "@mui/material";
import { memo } from "react";
import { useController } from "react-hook-form";

interface TextInputFieldProps {
  name: string;
  label: string;
  placeholder?: string;
  helperText?: string;
  errorText?: string;
  required: boolean;
  multiline?: boolean;
  width?: number;
}

const TextInputField = memo(function TextInputField({
  name,
  label,
  helperText,
  errorText,
  required,
  multiline,
  width = 12,
}: TextInputFieldProps) {
  const {
    field,
    formState: { errors },
  } = useController({ name });

  const errorMessage = getErrorMessageForField(errors, field.name);

  const getDisplayHelperText = () => {
    if (errorMessage) {
      return errorText || errorMessage.message;
    }

    if (required && helperText && helperText?.length > 0) {
      return `${helperText} *`;
    }

    return helperText || "";
  };

  return (
    <Grid item xs={width}>
      <TextField
        {...field}
        error={Boolean(errorMessage)}
        fullWidth
        helperText={getDisplayHelperText()}
        label={`${label} ${multiline ? "(supports markdown syntax)" : ""}`}
        placeholder={`${label} ${
          multiline ? "(supports markdown syntax)" : ""
        }`}
        required={Boolean(required)}
        variant="filled"
        multiline={multiline}
        sx={{
          boxShadow: 0,
        }}
      />
    </Grid>
  );
});

TextInputField.displayName = "TextInputField";

export { TextInputField };
