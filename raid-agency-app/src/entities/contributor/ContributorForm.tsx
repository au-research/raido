import { CheckboxField } from "@/fields/CheckboxField";
import { TextInputField } from "@/fields/TextInputField";
import { Grid } from "@mui/material";

const ContributorForm = ({ index }: { index: number }) => {
  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`contributor.${index}.id`}
        label="ORCID ID"
        placeholder="ORCID ID"
        required={true}
        width={8}
      />

      <CheckboxField
        name={`contributor.${index}.leader`}
        label="Leader?"
        width={2}
      />
      <CheckboxField
        name={`contributor.${index}.contact`}
        label="Contact?"
        width={2}
      />
    </Grid>
  );
};

ContributorForm.displayName = "ContributorDetailsFormComponent";
export default ContributorForm;
