import { ContributorRole, RaidDto } from "@/generated/raid";
import contributorRole from "@/references/contributor_role.json";
import { Autocomplete, TextField } from "@mui/material";
import { Control, Controller, FieldErrors } from "react-hook-form";

interface ContributorDetailsFormComponentProps {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
}

export default function ContributorRoleDetailsFormComponent({
  control,
  index,
}: ContributorDetailsFormComponentProps) {
  const contributorRoleReference: ContributorRole[] = contributorRole.map(
    (el) => ({
      id: el.uri,
      schemaUri: el.uri,
    })
  );

  return (
    <Controller
      key={index}
      name={`contributor.${index}.role`}
      control={control}
      defaultValue={[]}
      render={({ field }) => {
        return (
          <Autocomplete
            {...field}
            key={index}
            multiple
            size="small"
            options={contributorRoleReference.sort((a, b) =>
              a.id.localeCompare(b.id)
            )}
            getOptionLabel={(option) => {
              const splitLength = option.id.split("/").length;
              return option.id.split("/")[splitLength - 2];
            }}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            onChange={(event, newValue) => {
              const correctedNewValue = newValue.map((el) => ({
                schemaUri: "https://credit.niso.org/",
                id: el.id,
              }));
              field.onChange(correctedNewValue);
            }}
            
            renderInput={(params) => {
              return <TextField {...params} variant="outlined" />;
            }}
          />
        );
      }}
    />
  );
}
