import raidConfig from "@/../raid.config.json";
import { CheckboxField } from "@/fields/CheckboxField";
import { TextInputField } from "@/fields/TextInputField";
import { Box, Grid } from "@mui/material";

const ContributorForm = ({ index }: { index: number }) => {
  return (
    <Grid container spacing={2}>
      {raidConfig.version === "2" && (
        <TextInputField
          name={`contributor.${index}.id`}
          label="ORCID URL"
          placeholder="ORCID URL"
          required={true}
          width={12}
        />
      )}
      {raidConfig.version === "3" && (
        <TextInputField
          name={`contributor.${index}.email`}
          label="Email"
          placeholder="Email"
          required={true}
          width={12}
        />
      )}

      <CheckboxField
        name={`contributor.${index}.leader`}
        label="Leader?"
        width={6}
      />
      <CheckboxField
        name={`contributor.${index}.contact`}
        label="Contact?"
        width={6}
      />
    </Grid>
  );
};

ContributorForm.displayName = "ContributorDetailsFormComponent";
export default ContributorForm;
