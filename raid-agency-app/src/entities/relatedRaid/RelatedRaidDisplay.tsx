import DisplayCard from "@/components/DisplayCard";
import DisplayItem from "@/components/DisplayItem";
import { useMapping } from "@/contexts/mapping/useMapping";
import type { RelatedRaid } from "@/generated/raid";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { memo, useMemo } from "react";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No related RAiDs defined
  </Typography>
));

const RelatedRaidItem = memo(
  ({ relatedRaid, i }: { relatedRaid: RelatedRaid; i: number }) => {
    const { generalMap } = useMapping();

    const relatedRaidTypeMappedValue = useMemo(
      () => generalMap.get(String(relatedRaid.type?.id)) ?? "",
      [relatedRaid.type?.id]
    );

    return (
      <>
        <Typography variant="body1">Related RAiD #{i + 1}</Typography>
        <Grid container spacing={2}>
          <DisplayItem
            label="Related RAiD"
            value={relatedRaid.id}
            link={relatedRaid.id}
            width={6}
          />
          <DisplayItem
            label="Type"
            value={relatedRaidTypeMappedValue}
            width={6}
          />
        </Grid>
      </>
    );
  }
);

const RelatedRaidDisplay = memo(({ data }: { data: RelatedRaid[] }) => (
  <DisplayCard
    data={data}
    labelPlural="Related RAiDs"
    children={
      <>
        {data.length === 0 && <NoItemsMessage />}
        <Stack gap={2} divider={<Divider />}>
          {data.map((relatedRaid, i) => (
            <RelatedRaidItem
              relatedRaid={relatedRaid}
              key={crypto.randomUUID()}
              i={i}
            />
          ))}
        </Stack>
      </>
    }
  />
));

NoItemsMessage.displayName = "NoItemsMessage";
RelatedRaidItem.displayName = "RelatedRaidItem";
RelatedRaidDisplay.displayName = "RelatedRaidDisplay";

export default RelatedRaidDisplay;
