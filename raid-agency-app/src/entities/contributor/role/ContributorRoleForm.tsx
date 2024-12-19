import { TextSelectField } from "@/fields/TextSelectField";
import contributorRole from "@/references/contributor_role.json";
import { Grid } from "@mui/material";
import { memo, useMemo } from "react";

const ContributorRoleForm = memo(
  ({ parentIndex, index }: { parentIndex: number; index: number }) => {
    const contributorRoleReference = useMemo(
      () =>
        contributorRole.map((el) => ({
          value: el.uri,
          label: el.uri,
        })),
      []
    );

    return (
      <Grid container columnSpacing={2}>
        <TextSelectField
          options={contributorRoleReference}
          name={`contributor.${parentIndex}.role.${index}.id`}
          label="Role"
          placeholder="Role"
          required={true}
          width={6}
        />
      </Grid>
    );
  }
);

ContributorRoleForm.displayName = "ContributorRoleDetailsFormComponent";

export default ContributorRoleForm;
