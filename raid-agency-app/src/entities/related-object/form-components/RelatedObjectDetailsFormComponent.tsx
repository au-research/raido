import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import relatedObjectCategory from "@/references/related_object_category.json";
import relatedObjectType from "@/references/related_object_type.json";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import { Grid, IconButton, Stack, Tooltip } from "@mui/material";

export default function RelatedObjectDetailsFormComponent({
  index,
  handleRemoveRelatedObject,
}: {
  index: number;
  handleRemoveRelatedObject: (index: number) => void;
}) {
  return (
    <Stack direction="row" alignItems="flex-start" gap={1}>
      <Grid container spacing={2}>
        <TextInputField
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
