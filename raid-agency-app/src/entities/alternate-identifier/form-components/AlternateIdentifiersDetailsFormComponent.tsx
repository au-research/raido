import { TextInputField } from "@/fields/TextInputField";
import { RaidDto } from "@/generated/raid";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import { Grid, IconButton, Stack, Tooltip } from "@mui/material";
import { Control, FieldErrors } from "react-hook-form";

interface AlternateIdentifiersDetailsFormComponentProps {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveAlternateIdentifier: (index: number) => void;
}

export default function AlternateIdentifiersDetailsFormComponent({
  control,
  index,
  errors,
  handleRemoveAlternateIdentifier,
}: AlternateIdentifiersDetailsFormComponentProps) {
  return (
    <Stack direction="row" alignItems="flex-start" gap={1}>
      <Grid container spacing={2}>
        <TextInputField
          control={control}
          errors={errors}
          width={6}
          formFieldProps={{
            name: `alternateIdentifier.${index}.id`,
            type: "text",
            label: "Alternate Identifier ID",
            placeholder: "Alternate Identifier ID",
            helperText: "",
            errorText: "",
            multiline: false,
          }}
        />
        <TextInputField
          control={control}
          errors={errors}
          width={6}
          formFieldProps={{
            name: `alternateIdentifier.${index}.type`,
            type: "text",
            label: "Alternate Identifier Type",
            placeholder: "Alternate Identifier Type",
            helperText: "",
            errorText: "",
            multiline: false,
          }}
        />
      </Grid>
      <Tooltip title="Remove alternate identifier" placement="right">
        <IconButton
          aria-label="Remove alternate identifier"
          onClick={() => handleRemoveAlternateIdentifier(index)}
        >
          <RemoveCircleOutlineIcon />
        </IconButton>
      </Tooltip>
    </Stack>
  );
}
