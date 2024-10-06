import { TextInputField } from "@/fields/TextInputField";
import { RaidDto } from "@/generated/raid";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import { Grid, IconButton, Stack, Tooltip } from "@mui/material";
import { Control, FieldErrors } from "react-hook-form";

interface FormComponentProps {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveItem: (index: number) => void;
}

export default function AlternateUrlsDetailsFormComponent({
  control,
  index,
  errors,
  handleRemoveItem,
}: FormComponentProps) {
  const entityKey = "alternateUrl";
  const entityLabel = "Alternate URL";

  return (
    <Stack direction="row" alignItems="flex-start" gap={1}>
      <Grid container spacing={2}>
        <TextInputField
          width={12}
          formFieldProps={{
            name: `${entityKey}.${index}.url`,
            type: "text",
            label: `${entityLabel}`,
            placeholder: `${entityLabel}`,
            helperText: "",
            errorText: "",
          }}
        />
      </Grid>
      <Tooltip title={`Remove ${entityLabel}`} placement="right">
        <IconButton
          aria-label={`Remove ${entityLabel}`}
          onClick={() => handleRemoveItem(index)}
        >
          <RemoveCircleOutlineIcon />
        </IconButton>
      </Tooltip>
    </Stack>
  );
}
