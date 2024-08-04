import { RaidDto } from "@/generated/raid";
import { MenuItem, TextField } from "@mui/material";
import { Control, Controller } from "react-hook-form";

const languages = [
  { id: "deu", name: "German" },
  { id: "eng", name: "English" },
  { id: "fra", name: "French" },
  { id: "ita", name: "Italian" },
  { id: "spa", name: "Spanish" },
];

const LanguageSelector = ({
  name,
  control,
}: {
  name: string;
  control: Control<RaidDto>;
}) => {
  return (
    <Controller
      name={name as keyof RaidDto}
      control={control}
      render={({ field, fieldState: { error } }) => (
        <TextField
          {...field}
          select
          fullWidth
          variant="filled"
          size="small"
          label="Language"
          error={!!error}
          helperText={error?.message}
        >
          {languages.map((language) => (
            <MenuItem key={language.id} value={language.id}>
              {language.name}
            </MenuItem>
          ))}
        </TextField>
      )}
    />
  );
};

export default LanguageSelector;
