import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import generalMapping from "@/mapping/general-mapping.json";
import { Grid } from "@mui/material";
import { memo } from "react";

const RelatedRaidForm = memo(({ index }: { index: number }) => {
  const relatedRaidTypeOptions = generalMapping
    .filter((el) => el.field === "relatedRaid.type.schema")
    .map((el) => ({
      value: el.key,
      label: el.value,
    }));

  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`relatedRaid.${index}.id`}
        label="Related RAiD"
        placeholder="Related RAiD"
        required={true}
        width={8}
      />

      <TextSelectField
        options={relatedRaidTypeOptions}
        name={`relatedRaid.${index}.type.id`}
        label="Type"
        placeholder="Type"
        required={true}
        width={4}
      />
    </Grid>
  );
});

RelatedRaidForm.displayName = "RelatedRaidDetailsFormComponent";
export default RelatedRaidForm;
