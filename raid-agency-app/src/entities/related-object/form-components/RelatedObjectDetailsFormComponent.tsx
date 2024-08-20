import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import { RaidDto } from "@/generated/raid";
import relatedObjectType from "@/references/related_object_type.json";
import relatedObjectCategory from "@/references/related_object_category.json";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import { Grid, IconButton, Stack, Tooltip } from "@mui/material";
import { Control, FieldErrors } from "react-hook-form";

interface RelatedObjectDetailsFormComponentProps {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveRelatedObject: (index: number) => void;
}

export default function RelatedObjectDetailsFormComponent({
  control,
  index,
  errors,
  handleRemoveRelatedObject,
}: RelatedObjectDetailsFormComponentProps) {
  return (
    <Stack direction="row" alignItems="flex-start" gap={1}>
      <Grid container spacing={2}>
        <TextInputField
          control={control}
          errors={errors}
          width={12}
          formFieldProps={{
            name: `relatedObject.${index}.id`,
            type: "text",
            label: "Related Object ID",
            placeholder: "Related Object ID",
            helperText: "",
            errorText: "",
            multiline: false,
          }}
        />
        <TextSelectField
          control={control}
          errors={errors}
          width={3}
          options={relatedObjectType}
          formFieldProps={{
            name: `relatedObject.${index}.type.id`,
            type: "text",
            label: "Related Object Type",
            placeholder: "Related Object Type",
            helperText: "",
            errorText: "",
          }}
        />
        <TextSelectField
          control={control}
          errors={errors}
          width={3}
          options={relatedObjectCategory}
          formFieldProps={{
            name: `relatedObject.${index}.category[0].id`,
            type: "text",
            label: "Related Object Category",
            placeholder: "Related Object Category",
            helperText: "",
            errorText: "",
          }}
        />
      </Grid>
      <Tooltip title="Remove related object" placement="right">
        <IconButton
          aria-label="Remove related object"
          onClick={() => handleRemoveRelatedObject(index)}
        >
          <RemoveCircleOutlineIcon />
        </IconButton>
      </Tooltip>
    </Stack>
  );
}
