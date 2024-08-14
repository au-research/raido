import { RaidDto } from "@/generated/raid";
import language from "@/references/language.json";
import { Autocomplete, TextField } from "@mui/material";
import {
    Control,
    Controller
} from "react-hook-form";

export default function LanguageSelector({
  name,
  control,
}: {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  name: any;
  control: Control<RaidDto>;
}) {
  return (
    <Controller
      name={name}
      control={control}
      defaultValue=""
      rules={{ required: true }}
      render={({ field: { onChange, value }, fieldState: { error } }) => (
        <Autocomplete
          options={language}
          getOptionLabel={(option) => `${option.code}: ${option.name}`}
          value={
            language.find(
              (lang) => lang.code.toString() === value?.toString()
            ) || null
          }
          onChange={(_, newValue) => {
            onChange(newValue ? newValue.code : "");
          }}
          isOptionEqualToValue={(option, value) => {
            return option.code === value.code;
          }}
          renderInput={(params) => (
            <TextField
              {...params}
              size="small"
              label="Language"
              required
              error={!!error}
              helperText={error?.message}
            />
          )}
        />
      )}
    />
  );
}
