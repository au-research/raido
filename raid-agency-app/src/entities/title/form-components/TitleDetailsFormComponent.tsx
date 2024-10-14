import LanguageSelector from "@/fields/LanguageSelector";
import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import { RaidDto } from "@/generated/raid";
import titleType from "@/references/title_type.json";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import { Grid, IconButton, Stack, Tooltip } from "@mui/material";
import { Control, FieldErrors } from "react-hook-form";

interface TitleDetailsFormComponentProps {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveTitle: (index: number) => void;
}

export default function TitleDetailsFormComponent({
  control,
  index,
  errors,
  handleRemoveTitle,
}: TitleDetailsFormComponentProps) {
  return (
    <Stack direction="row" alignItems="flex-start" gap={1}>
      <Grid container spacing={2}>
        <TextInputField
          width={12}
          formFieldProps={{
            name: `title.${index}.text`,
            type: "text",
            label: "Title Text",
            placeholder: "Title Text",
            helperText: "",
            errorText: "",
          }}
        />
        <TextSelectField
          width={3}
          options={titleType}
          formFieldProps={{
            name: `title.${index}.type.id`,
            type: "text",
            label: "Title Type",
            placeholder: "Title Type",
            helperText: "",
            errorText: "",
          }}
        />
        <LanguageSelector
          formFieldProps={{
            name: `title.${index}.language.id`,
            type: "text",
            label: "Title Type",
            placeholder: "Title Type",
            helperText: "",
            errorText: "",
          }}
          width={3}
        />
        <TextInputField
          width={3}
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
          width={3}
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
  );
}
