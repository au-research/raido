import LanguageSelector from "@/components/LanguageSelector";
import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import { RaidDto } from "@/generated/raid";
import descriptionType from "@/references/description_type.json";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import { Grid, IconButton, Stack, Tooltip } from "@mui/material";
import { Control, FieldErrors } from "react-hook-form";

interface DescriptionDetailsFormComponentProps {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveDescription: (index: number) => void;
}

export default function DescriptionDetailsFormComponent({
  control,
  index,
  errors,
  handleRemoveDescription,
}: DescriptionDetailsFormComponentProps) {
  return (
    <Stack direction="row" alignItems="flex-start" gap={1}>
      <Grid container spacing={2}>
        <TextInputField
          control={control}
          errors={errors}
          width={12}
          formFieldProps={{
            name: `description.${index}.text`,
            type: "text",
            label: "Description Text",
            placeholder: "Description Text",
            helperText: "",
            errorText: "",
            multiline: false,
          }}
        />
        <TextSelectField
          control={control}
          errors={errors}
          width={3}
          options={descriptionType}
          formFieldProps={{
            name: `description.${index}.type.id`,
            type: "text",
            label: "Description Type",
            placeholder: "Description Type",
            helperText: "",
            errorText: "",
          }}
        />
        <LanguageSelector
          name={`description.${index}.language.id`}
          control={control}
          width={3}
        />
      </Grid>
      <Tooltip title="Remove description" placement="right">
        <IconButton
          aria-label="Remove description"
          onClick={() => handleRemoveDescription(index)}
        >
          <RemoveCircleOutlineIcon />
        </IconButton>
      </Tooltip>
    </Stack>
  );
}