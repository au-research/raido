import { DisplayItem } from "@/components/DisplayItem";
import { ContributorPosition } from "@/generated/raid";
import mapping from "@/mapping.json";
import { MappingElement } from "@/types";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import { Grid } from "@mui/material";

export const ContributorPositionItem = ({
  contributorPosition,
}: {
  contributorPosition: ContributorPosition;
}) => {
  const contributorPositionLabel = mapping.find(
    (el: MappingElement) => el.id === contributorPosition.id
  )?.value;
  return (
    <Grid container spacing={2}>
      <DisplayItem label="Title" value={contributorPositionLabel} width={6} />
      <DisplayItem
        label="Start"
        value={dateDisplayFormatter(contributorPosition.startDate)}
        width={3}
      />
      <DisplayItem
        label="End"
        value={dateDisplayFormatter(contributorPosition.endDate)}
        width={3}
      />
    </Grid>
  );
};
