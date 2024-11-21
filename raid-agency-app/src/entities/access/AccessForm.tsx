import DateInputField from "@/fields/DateInputField";
import LanguageSelector from "@/fields/LanguageSelector";
import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import generalMapping from "@/general-mapping.json";
import { RaidDto } from "@/generated/raid";
import { Grid } from "@mui/material";
import { memo } from "react";
import { FieldErrors } from "react-hook-form";

const AccessForm = memo(({ errors }: { errors: FieldErrors<RaidDto> }) => {
  const accessTypeOptions = generalMapping
    .filter((el) => el.field === "access.type.id")
    .map((el) => ({
      value: el.key,
      label: el.value,
    }));

  return (
    <Grid container spacing={2}>
      <TextInputField
        name={`access.statement.text`}
        label="Access Statement"
        required={true}
        width={6}
      />

      <LanguageSelector name={`access.statement.language.id`} width={6} />

      <TextSelectField
        options={accessTypeOptions}
        name={`access.type.id`}
        label="Access Type"
        required={true}
        width={6}
      />

      <DateInputField
        name="access.embargoExpiry"
        label="Select Date"
        required={true}
        width={6}
      />
    </Grid>
  );
});

AccessForm.displayName = "AccessFormComponent";
export default AccessForm;
