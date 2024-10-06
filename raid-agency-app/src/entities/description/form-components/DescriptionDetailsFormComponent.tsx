import LanguageSelector from "@/fields/LanguageSelector";
import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import descriptionType from "@/references/description_type.json";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import { Grid, IconButton, Stack, Tooltip } from "@mui/material";

export default function DescriptionDetailsFormComponent({
  index,
  handleRemoveDescription,
}: {
  index: number;
  handleRemoveDescription: (index: number) => void;
}) {
  return (
    <Stack direction="row" alignItems="flex-start" gap={1}>
      <Grid container spacing={2}>
        <TextInputField
          width={12}
          formFieldProps={{
            name: `description.${index}.text`,
            type: "text",
            label: "Text",
            placeholder: "Text",
            helperText: "",
            errorText: "",
            multiline: false,
          }}
        />
        <TextSelectField
          width={3}
          options={descriptionType}
          formFieldProps={{
            name: `description.${index}.type.id`,
            type: "text",
            label: "Type",
            placeholder: "Type",
            helperText: "",
            errorText: "",
          }}
        />

        <LanguageSelector
          formFieldProps={{
            name: `description.${index}.language.id`,
            type: "text",
            label: "Language",
            placeholder: "Language",
            helperText: "",
            errorText: "",
          }}
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
