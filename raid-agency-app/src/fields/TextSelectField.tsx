import mapping from "@/mapping.json";
import { FormFieldProps } from "@/types";
import { getErrorMessageForField } from "@/utils";
import { Grid, MenuItem, TextField } from "@mui/material";
import { useController } from "react-hook-form";
export function TextSelectField({
  options,
  formFieldProps,
  width = 12,
}: {
  options: any[];
  formFieldProps: FormFieldProps;
  width?: number;
}) {
  const { errorText, helperText, label, placeholder } = formFieldProps;
  const { field, formState } = useController(formFieldProps);
  const { errors } = formState;

  const keyField = formFieldProps.keyField ? formFieldProps.keyField : "uri";
  const errorMessage = getErrorMessageForField(errors, field.name);
  const displayHelperText = errorMessage
    ? errorText
      ? errorText
      : errorMessage.message
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
        required
        select
        size="small"
        variant="filled"
      >
        {options.map((opt) => {
          const mappedValue = mapping.find(
            (el) => el.id === opt[keyField]
          )?.value;
          return (
            <MenuItem key={opt[keyField]} value={opt[keyField]}>
              {mappedValue ? mappedValue : opt[keyField]}
            </MenuItem>
          );
        })}
      </TextField>
    </Grid>
  );
}
