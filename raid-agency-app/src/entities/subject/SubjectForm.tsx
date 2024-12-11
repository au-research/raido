import { TextSelectField } from "@/fields/TextSelectField";
import subjectMapping from "@/mapping/data/subject-mapping.json";
import { Grid } from "@mui/material";
import { memo } from "react";

const SubjectForm = memo(({ index }: { index: number }) => {
  return (
    <Grid container spacing={2}>
      <TextSelectField
        options={subjectMapping
          .sort((a, b) => a.value.localeCompare(b.value))
          .map((subject) => ({
            label: subject.value,
            value: subject.definition,
          }))}
        name={`subject.${index}.id`}
        label="Type"
        placeholder="Type"
        required={true}
        width={12}
      />
    </Grid>
  );
});

SubjectForm.displayName = "SubjectDetailsFormComponent";
export default SubjectForm;
