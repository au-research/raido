import LanguageSelector from "@/fields/LanguageSelector";
import { TextInputField } from "@/fields/TextInputField";
import { Grid } from "@mui/material";
import { memo } from "react";

const SpatialCoveragePlaceForm = memo(
  ({ parentIndex, index }: { parentIndex: number; index: number }) => {
    return (
      <Grid container columnSpacing={2}>
        <TextInputField
          name={`spatialCoverage.${parentIndex}.place.${index}.text`}
          label="Keyword"
          placeholder="Keyword"
          required={true}
          width={6}
        />
        <LanguageSelector
          name={`spatialCoverage.${parentIndex}.place.${index}.language.id`}
          width={6}
        />
      </Grid>
    );
  }
);

SpatialCoveragePlaceForm.displayName = "SpatialCoveragePlaceForm";
export default SpatialCoveragePlaceForm;
