import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import generalMapping from "@/mapping/data/general-mapping.json";
import { Grid } from "@mui/material";
import { memo } from "react";

const ContributorPositionForm = memo(
  ({ parentIndex, index }: { parentIndex: number; index: number }) => {
    const contributorPositionOptions = generalMapping
      .filter((el) => el.field === "contributor.position.id")
      .map((el) => ({
        value: el.key,
        label: el.value,
      }));
    return (
      <Grid container columnSpacing={2}>
        <TextSelectField
          options={contributorPositionOptions}
          name={`contributor.${parentIndex}.position.${index}.id`}
          label="Position"
          placeholder="Position"
          required={true}
          width={6}
        />
        <TextInputField
          name={`contributor.${parentIndex}.position.${index}.startDate`}
          label="Start Date"
          placeholder="Start Date"
          required={true}
          width={3}
        />
        <TextInputField
          name={`contributor.${parentIndex}.position.${index}.endDate`}
          label="end Date"
          placeholder="end Date"
          required={true}
          width={3}
        />
      </Grid>
    );
  }
);

ContributorPositionForm.displayName = "ContributorPositionDetailsFormComponent";
export default ContributorPositionForm;
