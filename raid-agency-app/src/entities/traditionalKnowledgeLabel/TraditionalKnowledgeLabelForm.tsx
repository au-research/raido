import { TextInputField } from "@/fields/TextInputField";
import { Grid } from "@mui/material";
import { memo } from "react";

const TraditionalKnowledgeLabelForm = memo(({ index }: { index: number }) => {
  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`traditionalKnowledgeLabel.${index}.id`}
        label="Text"
        placeholder="Text"
        required={true}
        width={12}
      />
    </Grid>
  );
});

TraditionalKnowledgeLabelForm.displayName = "TraditionalKnowledgeLabelForm";
export default TraditionalKnowledgeLabelForm;
