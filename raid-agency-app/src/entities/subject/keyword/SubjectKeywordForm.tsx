import LanguageSelector from "@/fields/LanguageSelector";
import { TextInputField } from "@/fields/TextInputField";
import { Grid } from "@mui/material";
import { memo } from "react";

const SubjectKeywordForm = memo(
  ({ parentIndex, index }: { parentIndex: number; index: number }) => {
    return (
      <Grid container columnSpacing={2}>
        <TextInputField
          name={`subject.${parentIndex}.keyword.${index}.text`}
          label="Keyword"
          placeholder="Keyword"
          required={true}
          width={6}
        />
        <LanguageSelector
          name={`subject.${parentIndex}.keyword.${index}.language.id`}
          width={6}
        />
      </Grid>
    );
  }
);

SubjectKeywordForm.displayName = "SubjectKeywordForm";
export default SubjectKeywordForm;
