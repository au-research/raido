import { TextInputField } from "@/fields/TextInputField";
import { RaidDto } from "@/generated/raid";
import mapping from "@/mapping.json";
import titleType from "@/references/title_type.json";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import {
  Grid,
  IconButton,
  MenuItem,
  Stack,
  TextField,
  Tooltip,
} from "@mui/material";
import { Control, Controller, FieldErrors } from "react-hook-form";
import LanguageSelector from "@/components/LanguageSelector";

export const TitleDetailsForm = ({
  control,
  index,
  errors,
  handleRemoveTitle,
}: {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveTitle: (index: number) => void;
}) => {
  return (
    <Controller
      control={control}
      name={`title.${index}`}
      render={({ field }) => (
        <Stack direction="row" alignItems="flex-start" gap={1}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={12} md={7}>
              <TextField
                {...field}
                value={field.value?.text || ""}
                size="small"
                required
                fullWidth
                label="Title"
                error={!!errors?.title?.[index]?.text}
                helperText={errors?.title?.[index]?.text?.message}
                onChange={(event) =>
                  field.onChange({ ...field.value, text: event.target.value })
                }
                data-testid="title-input"
              />
            </Grid>
            <Grid item xs={12} sm={6} md={2}>
              <TextField
                select
                value={field.value?.type?.id || ""}
                size="small"
                required
                fullWidth
                label="Type"
                onChange={(event) =>
                  field.onChange({
                    ...field.value,
                    type: { ...field.value?.type, id: event.target.value },
                  })
                }
              >
                {titleType.map((type) => (
                  <MenuItem key={type.uri} value={type.uri}>
                    {mapping.find((el) => el.id === type.uri)?.value}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <LanguageSelector
                name={`title.${index}.language.id`}
                control={control}
              />
            </Grid>
            <TextInputField
              control={control}
              errors={errors}
              width={6}
              formFieldProps={{
                name: `title.${index}.startDate`,
                type: "text",
                label: "Title Start Date",
                placeholder: "Title Start Date",
                helperText: "Title Start Date",
                errorText: "must be YYYY or YYYY-MM or YYYY-MM-DD",
              }}
            />

            <TextInputField
              control={control}
              errors={errors}
              width={6}
              formFieldProps={{
                name: `title.${index}.endDate`,
                type: "text",
                label: "Title End Date",
                placeholder: "Title End Date",
                helperText: "Title End Date",
                errorText: "must be YYYY or YYYY-MM or YYYY-MM-DD",
                required: false,
              }}
            />
          </Grid>
          <Tooltip title="Remove title" placement="right">
            <IconButton
              aria-label="Remove title"
              onClick={() => handleRemoveTitle(index)}
            >
              <RemoveCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        </Stack>
      )}
    />
  );
};
