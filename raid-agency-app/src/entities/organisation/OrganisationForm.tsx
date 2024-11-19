import { TextInputField } from "@/fields/TextInputField";
import { Grid } from "@mui/material";
import { memo } from "react";

const OrganisationForm = memo(({ index }: { index: number }) => {
  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`organisation.${index}.id`}
        label="ROR ID"
        placeholder="ROR ID"
        required={true}
        width={6}
      />
    </Grid>
  );
});

OrganisationForm.displayName = "OrganisationDetailsFormComponent";
export default OrganisationForm;
