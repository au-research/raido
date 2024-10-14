import { DisplayItem } from "@/components/DisplayItem";
import { OrganisationRole } from "@/generated/raid";
import mapping from "@/mapping.json";
import { MappingElement } from "@/types";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import { Grid } from "@mui/material";
export const OrganisationRoleItem = ({ item }: { item: OrganisationRole }) => {
  const itemPositionLabel = mapping.find(
    (el: MappingElement) => el.id === item.id
  )?.value;
  return (
    <Grid container spacing={2}>
      <DisplayItem label="Title" value={itemPositionLabel} width={6} />
      <DisplayItem
        label="Start"
        value={dateDisplayFormatter(item.startDate)}
        width={3}
      />
      <DisplayItem
        label="End"
        value={dateDisplayFormatter(item.endDate)}
        width={3}
      />
    </Grid>
  );
};
