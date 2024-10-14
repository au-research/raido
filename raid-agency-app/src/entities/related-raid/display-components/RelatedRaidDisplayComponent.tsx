import { DisplayItem } from "@/components/DisplayItem";
import { RelatedRaid } from "@/generated/raid";
import mapping from "@/mapping.json";
import { MappingElement } from "@/types";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

const entityLabelPlural = "Related RAiDs";

const NoContentMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No {entityLabelPlural} defined
  </Typography>
);

const RelatedRaidItem = ({ item }: { item: RelatedRaid }) => {
  let raidHandle = null;

  try {
    const url = new URL(item?.id || "");
    raidHandle = url.pathname.substring(1);
  } catch (error) {
    console.log("Related RAiDs: Invalid URL:", item?.id);
  }

  return (
    <Grid container spacing={2}>
      <DisplayItem
        label="ID"
        value={raidHandle || "Invalid RAiD link"}
        link={raidHandle}
        width={6}
      />
      <DisplayItem
        label="Type"
        value={
          mapping.find((el: MappingElement) => el.id === item.type?.id)?.value
        }
        width={6}
      />
    </Grid>
  );
};

export default function RelatedRaidDisplayComponent({
  items,
}: {
  items: RelatedRaid[] | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Related RAiDs" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {!items || items.length === 0 ? (
            <NoContentMessage />
          ) : (
            items.map((item, i) => (
              <Stack key={i} direction="row" alignItems="center" gap={2}>
                <RelatedRaidItem item={item} />
              </Stack>
            ))
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
