import LanguageSelector from "@/fields/LanguageSelector";
import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import generalMapping from "@/mapping/data/general-mapping.json";
import { Grid } from "@mui/material";
import { memo, useMemo } from "react";

const TitleForm = memo(({ index }: { index: number }) => {
  const titleTypeOptions = useMemo(
    () =>
      generalMapping
        .filter((el) => el.field === "title.type.schema")
        .map((el) => ({
          value: el.key,
          label: el.value,
        })),
    []
  );

  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`title.${index}.text`}
        label="Text"
        placeholder="Text"
        required={true}
        width={12}
      />
      <TextSelectField
        options={titleTypeOptions}
        name={`title.${index}.type.id`}
        label="Type"
        placeholder="Type"
        required={true}
        width={3}
      />
      <LanguageSelector name={`title.${index}.language.id`} width={3} />
      <TextInputField
        name={`title.${index}.startDate`}
        label="Start Date"
        placeholder="Start Date"
        required={true}
        width={3}
      />
      <TextInputField
        name={`title.${index}.endDate`}
        label="End Date"
        placeholder="End Date"
        required={false}
        width={3}
      />
    </Grid>
  );
});

TitleForm.displayName = "TitleForm";
export default TitleForm;
