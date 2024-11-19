import { ContributorRole } from "@/generated/raid";
import { Chip, Grid } from "@mui/material";
import { memo } from "react";

const ContributorRoleItem = memo(
  ({ contributorRole }: { contributorRole: ContributorRole }) => {
    const contributorRoleLabel = contributorRole.id
      .toString()
      .split("/")
      .slice(-2)[0];

    return (
      <Grid item>
        <Chip
          label={contributorRoleLabel}
          size="small"
          sx={{ borderRadius: 1, boxShadow: 1 }}
        />
      </Grid>
    );
  }
);

ContributorRoleItem.displayName = "ContributorRoleItem";
export default ContributorRoleItem;
