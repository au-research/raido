import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import { RaidDto } from "@/generated/raid";
import contributorPosition from "@/references/contributor_position.json";
import { Grid, Stack } from "@mui/material";
import { Control, FieldErrors } from "react-hook-form";

interface ContributorPositionDetailsFormComponentProps {
  control: Control<RaidDto>;
  contributorIndex: number;
  positionIndex: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveContributorPosition?: (index: number) => void;
}

export default function ContributorPositionDetailsFormComponent({
  control,
  contributorIndex,
  positionIndex,
  errors,
}: ContributorPositionDetailsFormComponentProps) {
  return (
    <Stack direction="row" gap={1} width="100%">
      <Grid container columnSpacing={2}>
        <TextSelectField
          width={6}
          options={contributorPosition}
          formFieldProps={{
            name: `contributor.${contributorIndex}.position.${positionIndex}.id`,
            type: "text",
            label: "Position",
            placeholder: "Position",
            helperText: "",
            errorText: "",
          }}
        />
        <TextInputField
          width={3}
          formFieldProps={{
            name: `contributor.${contributorIndex}.position.${positionIndex}.startDate`,
            type: "text",
            label: "Start Date",
            placeholder: "Start Date",
            helperText: "",
            errorText: "",
          }}
        />
        <TextInputField
          width={3}
          formFieldProps={{
            name: `contributor.${contributorIndex}.position.${positionIndex}.endDate`,
            type: "text",
            label: "End Date",
            placeholder: "End Date",
            helperText: "",
            errorText: "",
          }}
        />
      </Grid>
    </Stack>
  );
}
