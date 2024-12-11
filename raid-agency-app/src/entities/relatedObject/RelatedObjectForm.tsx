import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import generalMapping from "@/mapping/data/general-mapping.json";
import { Grid } from "@mui/material";
import { memo, useMemo } from "react";

const RelatedObjectForm = memo(({ index }: { index: number }) => {
  const relatedObjectTypeOptions = useMemo(
    () =>
      generalMapping
        .filter((el) => el.field === "relatedObject.type.id")
        .map((el) => ({
          value: el.key,
          label: el.value,
        })),
    []
  );

  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`relatedObject.${index}.id`}
        label="Related Object ID"
        placeholder="Related Object ID"
        required={true}
        width={8}
      />

      <TextSelectField
        options={relatedObjectTypeOptions}
        name={`relatedObject.${index}.type.id`}
        label="Type"
        placeholder="Type"
        required={true}
        width={4}
      />
    </Grid>
  );
});

RelatedObjectForm.displayName = "RelatedObjectDetailsFormComponent";
export default RelatedObjectForm;
