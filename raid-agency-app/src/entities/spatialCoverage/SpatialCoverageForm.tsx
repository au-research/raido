import { TextInputField } from "@/fields/TextInputField";
import { Grid } from "@mui/material";
import { memo } from "react";

const SpatialCoverageForm = memo(({ index }: { index: number }) => {
  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`spatialCoverage.${index}.id`}
        label="ID"
        placeholder="ID"
        required={true}
        width={12}
      />
    </Grid>
  );
});

SpatialCoverageForm.displayName = "SpatialCoverageDetailsFormComponent";
export default SpatialCoverageForm;
