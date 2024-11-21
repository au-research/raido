import LanguageSelector from "@/fields/LanguageSelector";
import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import generalMapping from "@/general-mapping.json";
import { Grid } from "@mui/material";
import { memo, useMemo } from "react";

const DescriptionForm = memo(({ index }: { index: number }) => {
  const descriptionTypeOptions = useMemo(
    () =>
      generalMapping
        .filter((el) => el.field === "description.type.id")
        .map((el) => ({
          value: el.key,
          label: el.value,
        })),
    []
  );

  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`description.${index}.text`}
        label="Text"
        placeholder="Text"
        required={true}
        multiline={true}
        width={12}
      />
      <TextSelectField
        options={descriptionTypeOptions}
        name={`description.${index}.type.id`}
        label="Type"
        placeholder="Type"
        required={true}
        width={6}
      />
      <LanguageSelector name={`description.${index}.language.id`} width={6} />
    </Grid>
  );
});

DescriptionForm.displayName = "DescriptionDetailsFormComponent";
export default DescriptionForm;
