import DisplayItem from "@/components/DisplayItem";
import { useMapping } from "@/contexts/mapping/useMapping";
import { ContributorPosition } from "@/generated/raid";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import { Grid } from "@mui/material";
import { memo, useMemo } from "react";

const ContributorPositionItem = memo(
  ({ contributorPosition }: { contributorPosition: ContributorPosition }) => {
    const { generalMap } = useMapping();

    const contributorPositionMappedValue = useMemo(
      () => generalMap.get(String(contributorPosition.id)) ?? "",
      [contributorPosition.id]
    );

    return (
      <Grid container spacing={2}>
        <DisplayItem
          label="Position"
          value={contributorPositionMappedValue}
          width={6}
        />
        <DisplayItem
          label="Start Date"
          value={dateDisplayFormatter(contributorPosition.startDate)}
          width={3}
        />
        <DisplayItem
          label="End Date"
          value={dateDisplayFormatter(contributorPosition.endDate)}
          width={3}
        />
      </Grid>
    );
  }
);

ContributorPositionItem.displayName = "ContributorPositionItem";
export default ContributorPositionItem;
