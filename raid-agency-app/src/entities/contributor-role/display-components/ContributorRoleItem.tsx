
import { ContributorRole } from "@/generated/raid";
import {
  Chip,
  Grid
} from "@mui/material";

export const ContributorRoleItem = ({
  contributorRole,
}: {
  contributorRole: ContributorRole;
}) => {
  const contributorRoleSplit = contributorRole.id.toString().split("/");
  const contributorRoleSplitLength = contributorRoleSplit.length;
  const contributorRoleLabel =
    contributorRoleSplit[contributorRoleSplitLength - 2];

  return (
    <Grid item>
      <Chip label={contributorRoleLabel} size="small" />
    </Grid>
  );
};
