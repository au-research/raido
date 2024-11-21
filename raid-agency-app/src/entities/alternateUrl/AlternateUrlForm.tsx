import { TextInputField } from "@/fields/TextInputField";
import { Grid } from "@mui/material";

const AlternateUrlForm = ({ index }: { index: number }) => {
  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`alternateUrl.${index}.url`}
        label="Alternate URL"
        placeholder="Alternate URL"
        required={true}
        width={12}
      />
    </Grid>
  );
};

AlternateUrlForm.displayName = "AlternateUrlsDetailsFormComponent";
export default AlternateUrlForm;
