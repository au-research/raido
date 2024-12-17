import { TextInputField } from "@/fields/TextInputField";
import { Grid } from "@mui/material";
import { memo } from "react";

const AlternateIdentifierForm = memo(({ index }: { index: number }) => {
  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`alternateIdentifier.${index}.id`}
        label="ID"
        placeholder="ID"
        required={true}
        width={6}
      />
      <TextInputField
        name={`alternateIdentifier.${index}.type`}
        label="Type"
        placeholder="Type"
        required={true}
        width={6}
      />
    </Grid>
  );
});

AlternateIdentifierForm.displayName =
  "AlternateIdentifiersDetailsFormComponent";
export default AlternateIdentifierForm;
