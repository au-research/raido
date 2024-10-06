import language from "@/references/language.json";
import { FormFieldProps } from "@/types";
import { getErrorMessageForField } from "@/utils";
import { Autocomplete, Grid, TextField } from "@mui/material";
import { useController } from "react-hook-form";

export default function LanguageSelector({
  formFieldProps,
  width = 12,
}: {
  formFieldProps: FormFieldProps;
  width: number;
}) {
  const { errorText, helperText } = formFieldProps;

  const { field, formState } = useController(formFieldProps);
  const { errors } = formState;

  const errorMessage = getErrorMessageForField(errors, field.name);
  const displayHelperText = errorMessage
    ? errorText
      ? errorText
      : errorMessage.message
    : helperText;

  const { onChange, ...restField } = field;
  return (
    <Grid item xs={width}>
      <Autocomplete
        {...restField}
        options={language}
        getOptionLabel={(option) => `${option.code}: ${option.name}`}
        value={
          language.find(
            (lang) => lang.code.toString() === restField.value?.toString()
          ) || null
        }
        onChange={(_, newValue) => {
          onChange(newValue ? newValue.code : "");
        }}
        isOptionEqualToValue={(option, value) => {
          return option.code === value.code;
        }}
        renderInput={(params) => {
          // TODO: Display error on language selector
          return (
            <TextField
              {...params}
              variant="filled"
              size="small"
              label="Language"
              required
              helperText={displayHelperText}
            />
          );
        }}
      />
    </Grid>
  );
}
