import { TextInputField } from "@/fields/TextInputField";
import { Grid } from "@mui/material";
import { memo } from "react";

const DateForm = memo(() => {
  return (
    <Grid container spacing={2}>
      <TextInputField
        name="date.startDate"
        label="Start Date"
        required={true}
        width={3}
      />
      <TextInputField
        name="date.endDate"
        label="End Date"
        required={false}
        width={3}
      />
    </Grid>
  );
});

DateForm.displayName = "DateFormComponent";
export default DateForm;
